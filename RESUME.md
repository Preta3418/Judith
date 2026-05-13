# Judith Management System — 기술 포트폴리오 요약

> **Purpose:** Concise portfolio-ready summary for resume/interviews. One section per domain, key decisions with reasoning.

> **What belongs here:**
> - **Key architecture decisions** — choices that shaped the system design (e.g. why pessimistic over optimistic lock, why interface + @Profile for storage, why AWS region split per service). If a decision was non-trivial and worth explaining to a technical interviewer, it goes here.
> - **Things learned that are non-obvious** — constraints, trade-offs, or patterns that aren't immediately obvious from reading the code (e.g. AWS SNS requiring Tokyo region for Korean SMS, @ElementCollection eliminating a join entity, Optional.or() chaining for fallback). If someone could easily miss WHY it was done this way, it belongs here.
> - **Numbers and metrics** — response time improvements, query counts, error rates — add these only after actual testing. Do not estimate or invent numbers.
> - **NOT needed:** Routine CRUD decisions, obvious Spring patterns, anything already self-explanatory from the code.

> **Update Rules:**
> - Add new domains as sections when Phase 1 of that domain is complete
> - If it's a change or something learned **within an existing domain**, update that domain's section — do not add a new one
> - Keep each decision to 2–4 sentences max. This file should stay short enough to read in full
> - Written in **Korean** — snippets and field names in English

---

대학교 연극 동아리를 위한 풀스택 관리 시스템. Spring Boot 3 / Java 21 / AWS (EC2, RDS, S3, SNS) / Docker.

---

## User 도메인

**결정: 단일 엔티티 + 상태 enum**
`User` / `Graduate` 테이블 분리 설계로 시작했으나, 졸업 시 행을 이동하면 예매·메시지 등 모든 FK가 깨지는 문제를 발견. `UserStatus.INACTIVE`로 상태만 변경하는 방식으로 전환하여 참조 무결성 유지. 데이터 이동이 아닌 상태 전환이 FK를 가진 도메인에서 안전한 패턴임을 학습.

---

## Message 도메인 & AWS SNS

**결정: 한국 SMS 업체 → AWS SNS 전환**
한국 SMS 규정상 API 접근에 사업자등록번호 필요 — 학생 동아리는 획득 불가. AWS SNS로 전환 후 E.164 전화번호 변환기(`+82` 처리)와 `MessageFailure` 엔티티(재시도·성공률 추적용)를 구현했다.

---

## Reservation 도메인

**결정: 비관적 잠금 (낙관적 잠금 불채택)**
```java
@Lock(LockType.PESSIMISTIC_WRITE)
Optional<EventSchedule> findByIdWithLock(Long id);
```
연극 공연의 좌석은 수용 인원이 작고 경합이 높다. 낙관적 잠금은 재시도 로직이 복잡하고, 예매 실패가 지연보다 나쁜 UX이므로 비관적 잠금을 선택.

**결정: remainingSeats 동적 계산**
잔여 좌석을 컬럼으로 저장하지 않고 서비스에서 `capacity - SUM(tickets)`으로 계산. 저장 값은 매 예매/취소마다 업데이트가 필요하고 동기화 실패 시 오래된 데이터 노출 위험이 있다. 계산 비용이 낮으므로 단일 진실 공급원 유지를 선택.

**리팩토링: EventSchedule 분리**
초기 `Event.date` 설계는 회차 선택이 불가능했다. `EventSchedule`(Event:Many-to-One) 엔티티를 추가하고 날짜·수용 인원·비관적 잠금을 이동시켰다 — 기존 기능을 유지하며 3일, 6커밋 소요.

---

## User-Season 도메인

**결정: @ElementCollection으로 시즌별 다중 역할**
```java
@ElementCollection
private Set<UserRole> userRoles = new HashSet<>();
```
`User.role` 단일 필드로는 시즌별 역할 이력 추적이 불가능했다. `UserSeason` 조인 엔티티에 `@ElementCollection`으로 별도 엔티티 없이 다중 역할 저장. JPA가 `user_season_roles` 테이블을 자동 생성하며, Set으로 중복을 방지한다.

**결정: 2단계 관리자 구조**
| 유형 | 구현 | 특징 |
|------|------|------|
| 슈퍼 관리자 | `User.isAdmin = true` | 영구, UI에서 숨김 |
| 운영 관리자 | UserSeason 역할 (LEADER 등) | 시즌별, UI에서 관리 |

---

## Entity 모범 사례 리팩토링

**결정: 클래스 레벨 @Setter/@Builder 제거**
엔티티에 클래스 레벨 `@Setter`를 두면 어디서든 필드를 변경할 수 있어 상태 변경 추적이 어렵고 부분 업데이트에 취약하다. 모든 엔티티에서 제거 후 `closeSeason()`, `activateSeason()` 같은 도메인 메서드로 교체. `@Builder`는 생성자로 이동하여 인스턴스화를 통제.

---

## File Storage 시스템

**결정: StorageService 인터페이스 + @Profile 전환**
```java
@Profile("local") class LocalStorageService implements StorageService { ... }
@Profile("prod")  class S3StorageService  implements StorageService { ... }
```
초기 로컬 저장 설계 시 `StorageService` 인터페이스를 정의해 두었다. S3 전환 시 구현체만 추가하고 `@Profile` 어노테이션으로 교체 완료 — `UploadController` 수정 제로. Spring DI가 프로필에 따라 올바른 구현체를 자동 주입한다.

**결정: AWS 리전 분리**
SNS SMS는 `ap-northeast-1`(도쿄)에서만 한국 번호 발송 가능. S3/EC2/RDS는 `ap-northeast-2`(서울). `AwsConfig`에서 `SnsClient`와 `S3Client`를 각자의 리전으로 별도 빈으로 등록하여 서비스별 리전 제약을 독립 관리.

---

## Season 도메인

**결정: 날짜 자동화 (사용자 입력 제거)**
`startDate`/`endDate`를 생성 시 입력받는 설계는 의미상 맞지 않다 — "시작일"은 실제 활성화 시점이어야 한다. `activateSeason()`과 `closeSeason()` 도메인 메서드가 `LocalDate.now()`를 자동으로 설정하도록 리팩토링.

**상태 머신:** PREPARING → ACTIVE → CLOSED (되돌리기 불가, 동시에 비-CLOSED 시즌 하나만 존재)

---

## 시즌 기반 접근 제어

**문제: 시즌 전환 시 운영진 잠금(Lockout)**
시즌 1이 CLOSED되고 시즌 2(PREPARING)를 생성하면, 시즌 2에 멤버가 없으므로 모든 운영진이 `hasFullAccess = false`가 되어 관리자 페이지 접근 불가 → 멤버 배정도 불가 → 교착 상태.

**해결 1: 유효 시즌 폴백**
```java
return seasonRepository.findByStatus(ACTIVE)
    .or(() -> seasonRepository.findByStatus(PREPARING))
    .or(() -> seasonRepository.findTopByStatusOrderByCreatedAtDesc(CLOSED));
```
`Optional.or()` 체이닝으로 3단계 폴백. 공백기에도 마지막 CLOSED 시즌의 역할을 기준으로 운영진 접근 유지.

**해결 2: @Transactional 원자적 시즌 생성**
시즌 생성과 멤버 배정을 단일 트랜잭션으로 묶어 "멤버 없는 시즌"이 커밋 전 외부에 노출되지 않도록 방지. 최소 1명의 Full Access 멤버 보유를 생성 시점에 검증.

**해결 3: UserRole.hasFullAccess() 중앙화**
`!Collections.disjoint(roles, FULL_ACCESS_ROLES)` 패턴이 4곳에서 중복. `UserRole` enum에 `static` 메서드로 추가하고 `FULL_ACCESS_ROLES`를 `private`으로 변경하여 단일 진실 공급원 확보.

---

## Notification 시스템

**결정: Notification + UserNotification 분리**
`Notification`은 메시지 내용 한 번 저장, `UserNotification`은 수신자별 읽음 상태 추적. 하나의 알림이 N명에게 발송될 때 콘텐츠를 중복 저장하지 않고, 각자의 읽음 상태를 독립적으로 관리할 수 있다.

**결정: Spring ApplicationEvent로 도메인 분리**
로그인 시 비밀번호 미변경 알림 생성 요구사항을 `ApplicationEvent`로 구현. `AuthController`는 `UserLoggedInEvent`를 발행만 하고, `NotificationEventListener`가 수신·처리한다. 인증 도메인이 알림 도메인을 직접 참조하지 않아 새 로그인 후속 동작은 리스너 추가만으로 확장 가능.

---

## Security & Authentication

**JWT 설계:**
```json
{ "sub": "studentNumber", "userId": 1, "hasFullAccess": true }
```
`hasFullAccess`를 토큰에 포함하여 매 요청마다 DB 조회 없이 권한 판단. `JwtAuthenticationFilter`에서 `hasFullAccess → ROLE_ADMIN/ROLE_USER`로 매핑.

**결정: URL 기반 3단계 접근 제어**
```
/api/public/**  → permitAll()
/api/admin/**   → hasRole("ADMIN")
/api/**         → authenticated()
```
SecurityConfig 3줄로 단순화. 엔드포인트 접근 수준을 URL에서 즉시 파악 가능.

**결정: @PreAuthorize vs authentication.getDetails()**
- URL에 userId가 있을 때: `@PreAuthorize("authentication.details == #userId")` — URL 바꿔치기 방지
- URL에 userId가 없을 때: `(Long) authentication.getDetails()` — JWT에서 직접 추출

**결정: Refresh Token 미적용**
사용자 20~30명, 비금융 데이터, 학기 중 간헐적 접속 패턴. Refresh Token은 `RefreshToken` 엔티티, 토큰 회전, 재사용 감지 메커니즘이 필요하여 이 규모에서는 복잡도가 보안 이점을 초과한다고 판단.

---

## DevOps & 인프라

**멀티스테이지 Docker 빌드**
빌드 스테이지(Gradle + JDK)와 실행 스테이지(JRE + JAR)를 분리. 최종 이미지에 빌드 도구가 포함되지 않아 이미지 크기를 대폭 절감.

**크로스 아키텍처 빌드**
Apple Silicon(ARM) 개발 환경에서 빌드한 이미지가 x86 EC2에서 `no matching manifest` 오류. `--platform linux/amd64` 플래그로 크로스 컴파일하여 해결.

**환경 분리**
`application-prod.properties`의 모든 시크릿을 `${ENV_VAR}` 참조, `docker run -e`로 주입. 동일 이미지가 로컬(H2, 로컬 파일)과 프로덕션(RDS MySQL, S3)에서 자동으로 다른 설정을 사용.

**인프라 현황:** EC2(t3.micro) + RDS MySQL(db.t4g.micro) + S3(seoul) + SNS(tokyo) + GHCR — 모두 운영 중. GitHub Actions, Nginx, Route 53, CloudFront 예정.

---

## Dashboard (Member Portal) — Phase 1

**문제:** 일반 팀원(배우, 스태프)이 로그인하면 "준비 중" 화면만 있었다. 시스템이 운영진 전용 도구가 되어버린 것.

**설계 결정:**
- **assertMembership 패턴**: 모든 Dashboard 엔드포인트에서 `existsByUserIdAndSeasonId()`로 멤버십 검증. 서비스의 `private` 메서드로 캡슐화 — 컨트롤러가 노출된 가드를 직접 호출하는 것은 책임을 잘못 분산시킨다.
- **공지 엔드포인트를 /api/admin/으로**: 서비스 레이어 권한 검사 없이 URL 컨벤션만으로 접근 제어. 기존 패턴과 일치.
- **중복 멤버 엔드포인트 없음**: `GET /api/public/seasons/{id}/users`가 이미 존재 — 새 엔드포인트 추가하지 않음.
- **생성자 빌더로 파생 필드 보호**: `myFullAccess`는 `myRoles`에서 자동 계산. 호출자가 `.myFullAccess(true)`로 임의 설정하는 것을 차단.

---

## 테스트

**결정: Mockito 단위 테스트 (`@ExtendWith(MockitoExtension.class)`)**
Spring 컨텍스트 없이 서비스 레이어만 격리하여 테스트. `@Mock`으로 의존 리포지토리를 대체하고 `@InjectMocks`로 서비스를 주입한다. `@SpringBootTest`보다 실행 속도가 빠르고, 검증하려는 비즈니스 로직과 무관한 DB/JPA 설정이 개입하지 않는다.

**DashboardServiceTest — 7개**
핵심 불변 조건 위주로 작성. `assertMembership` 가드(403 동작), `createSeasonNotification`의 ACTIVE 시즌 강제, `myFullAccess` 파생 필드가 `myRoles`에서 올바르게 계산되는지 확인. 단순 조회 메서드(getScripts, getNotifications)는 `assertMembership` + 레포지토리 호출이 전부이므로 테스트 우선순위에서 제외.

**ReservationServiceTest — 5개**
비관적 잠금 계층의 로직 검증. 초기 테스트 파일은 `EventRepository`를 목으로 설정했으나 `ReservationService`의 실제 의존성은 `EventScheduleRepository`여서 컴파일은 되지만 런타임에 NPE 발생. 올바른 의존성으로 교체하고 5개 케이스 완성: 정상 예매, 이벤트 마감, 예매 기한 초과, 중복 전화번호, 잔여 좌석 부족.
