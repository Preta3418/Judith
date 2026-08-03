# Spring Practice — 도서관 대출 시스템

> 목적: Spring 도메인 하나를 처음부터 다시 만드는 감을 유지하는 것. Judith와 무관한 별도 도메인으로,
> Board를 처음 설계했을 때와 비슷한 복잡도를 갖도록 설계했다.

## 도메인: 도서관 대출 시스템

동네 도서관 관리 시스템. 회원은 책을 빌리고, 반납하고, 대기(예약)할 수 있다.
사서는 책과 사본을 관리하고, 연체 리포트를 뽑는다.

### 엔티티 (총 6개)

| 엔티티 | 역할 |
|---|---|
| `Category` | 책의 카테고리 (소설, 과학, 역사 ...) |
| `Book` | 논리적 책 정보 (제목, 저자, ISBN, 카테고리) |
| `BookCopy` | 물리적 사본. 같은 책에 여러 사본이 있음. 각 사본은 상태와 상태(condition)를 가짐 |
| `Member` | 회원. tier(BASIC/PREMIUM)에 따라 동시 대출 한도가 다름 |
| `Loan` | 대출 기록. 회원-사본 pair. 반납되면 returnedAt 채워짐 |
| `Reservation` | 예약(대기). 회원-책 pair. 사본이 반납되면 대기 순번에 따라 처리 |

### 비즈니스 규칙 (서비스 레이어에서 강제)

**대출 (Borrow)**
- BASIC 회원: 동시 최대 3권
- PREMIUM 회원: 동시 최대 10권
- 대출 기간: 14일 (반납 예정일 = 대출일 + 14)
- 다음 조건이면 대출 불가:
  - 회원이 비활성 (`active = false`)
  - 회원의 대출 중 하나라도 연체 상태 (오늘 > dueAt AND returnedAt = null)
  - 동시 대출 한도 도달
  - 해당 책의 사용 가능한 사본(AVAILABLE)이 없음

**반납 (Return)**
- Loan의 returnedAt = 오늘, status = RETURNED
- 사본의 condition을 반납 시점 상태로 업데이트 (반납자가 신고)
- condition이 DAMAGED면 사본 status = LOST, 회원에게 벌금
- 그 외에는 사본 status = AVAILABLE
- 이 책에 대기자가 있으면 → 첫 번째 대기자에게 "준비됨" 상태 (알림은 스텁으로 로그만)

**연장 (Extend)**
- 한 loan당 최대 1회
- 대기자가 없어야 연장 가능
- 연장 시 dueAt = dueAt + 7일

**예약 (Reserve)**
- 회원이 이미 이 책을 대출 중이면 예약 불가
- 회원이 이 책에 이미 예약 중이면 예약 불가
- 위치 = 이 책에 이미 대기 중인 예약 수 + 1

**연체 벌금 계산**
- 하루당 100원
- 반납 시 계산되어 회원 계정에 청구 (실제 결제는 스텁)

### 접근 제어

간단히 유지: 두 역할만 있음.
- **Librarian (사서)**: 책·사본 CRUD, 모든 회원 조회, 연체 리포트
- **Member (회원)**: 자기 대출·예약 조회, 대출/반납/연장/예약

`Role` enum과 `currentUser.role`로 서비스에서 체크. Spring Security 없음 — 그냥 파라미터로 넘긴다고 가정.

---

## 파일 구조 (모두 만들어져 있음)

```
practice/spring/library/
    enums/
        BookStatus.java, BookCondition.java, LoanStatus.java, MemberTier.java, Role.java
    entity/
        Category.java, Book.java, BookCopy.java, Member.java, Loan.java, Reservation.java
    repository/
        CategoryRepository, BookRepository, BookCopyRepository, MemberRepository,
        LoanRepository, ReservationRepository
    dto/
        (BookRequest, BookResponse, BorrowRequest, LoanResponse,
         MemberResponse, ReservationResponse, OverdueReportRow)
    exception/
        BookNotAvailableException, LoanLimitExceededException, MemberInactiveException,
        OverdueBlockException, NotOnWaitlistException, EntityNotFoundException
    service/
        LibraryService.java  (책·사본·카테고리 관리)
        LoanService.java     (대출·반납·연장·예약 — 이게 제일 복잡)
    controller/
        LibraryController.java, LoanController.java
```

**중요:** 모든 클래스는 **plain Java**로 되어 있음 (@Entity, @Service, @Repository 등 없음).
실제로 돌리려면 어노테이션을 직접 붙여야 한다. 이유:
- Spring이 자동으로 wire 하면 컴파일만 되어도 앱이 시작할 때 이 도메인의 반쪽짜리 서비스가 로드되어 문제 생길 수 있음
- 이 연습의 핵심은 어노테이션도 스스로 붙이는 것

---

## 진행 방법

1. **엔티티 · enums 읽기** — 도메인 이해 (10분)
2. **README(이 파일) 비즈니스 규칙 정독** (5분)
3. **각 파일의 TODO 채우기** — 아래 순서 추천:
   - a) 리포지토리 derived query 이름 (`BookRepository` 등에 examples + TODO)
   - b) `LibraryService`의 CRUD 메서드 (쉬움; 워밍업)
   - c) `LoanService.borrow()` — 첫 번째 큰 도전
   - d) `LoanService.returnBook()` — 대기자 처리 로직 포함
   - e) `LoanService.extendLoan()` — 조건 여러 개
   - f) `LoanService.reserveBook()` — 위치 계산
   - g) `LoanService.getOverdueReport()` — 쿼리 조합
   - h) 컨트롤러 엔드포인트 채우기
4. **어노테이션 붙이고 실제로 돌려보기** (선택):
   - 엔티티에 `@Entity` + JPA 어노테이션
   - 리포지토리에 `@Repository`, `extends JpaRepository`
   - 서비스에 `@Service`, `@Transactional`
   - 컨트롤러에 `@RestController`, `@RequestMapping`

---

## 핵심 학습 포인트

- **의존성 순서** — Loan은 Member와 BookCopy를 참조하므로 그 순서로 만들기
- **가드 순서** — `borrow()` 안에서 어떤 순서로 검증할지 (회원 활성 → 연체 없음 → 한도 → 사본 available)
- **트랜잭션 경계** — `returnBook()`은 여러 엔티티를 수정하므로 하나의 @Transactional로
- **파생 쿼리 vs @Query** — 대부분 파생 쿼리로 되지만 리포트 같은 건 @Query로 표현
- **DTO 매핑** — 엔티티를 그대로 반환하지 말고 Response DTO로 변환
- **예외 계층** — Business 예외를 만들고 GlobalExceptionHandler가 잡도록

각 파일에 어떤 메서드가 어떤 규칙을 강제해야 하는지 자세한 TODO 코멘트가 있다. 힌트로 몇 개는 완성해뒀다.

## 완성 후 자체 점검

- [ ] BASIC 회원이 4번째 대출 시도 → `LoanLimitExceededException`
- [ ] 연체된 회원이 대출 시도 → `OverdueBlockException`
- [ ] 대기자 있는 loan을 연장 시도 → 실패
- [ ] 반납 시 대기자 첫 번째가 알림 스텁 발동
- [ ] 손상된 사본 반납 → 사본 LOST + 벌금 청구
- [ ] 연체 리포트 → 오늘 이후 dueAt 지난 loan만 나옴
