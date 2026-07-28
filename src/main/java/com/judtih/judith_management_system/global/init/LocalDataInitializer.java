package com.judtih.judith_management_system.global.init;

import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.season.SeasonRepository;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SeasonRepository seasonRepository;
    private final UserSeasonRepository userSeasonRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) return;
        seed();
    }

    private void seed() {
        // Super admin — passwordChanged=true so the modal doesn't pop up
        String adminPass = passwordEncoder.encode("1234");
        User admin = User.builder()
                .name("관리자")
                .studentNumber("admin")
                .password(adminPass)
                .isAdmin(true)
                .build();
        admin.updatePassword(adminPass);
        userRepository.save(admin);

        // Active members — default password = studentNumber (same as prod behaviour)
        User leader     = saveMember("김회장", "20231001", "01011111111");
        User producer   = saveMember("이연출", "20231002", "01022222222");
        User actor1     = saveMember("박배우", "20231003", "01033333333");
        User actor2     = saveMember("최배우", "20231004", "01044444444");
        User stageDes   = saveMember("정무대", "20231005", "01055555555");
        User soundOp    = saveMember("한음향", "20231006", "01066666666");

        // Inactive member
        User inactive = saveMember("윤졸업", "20220001", "01077777777");
        inactive.deactivate();
        userRepository.save(inactive);

        // Currently active season
        Season season = new Season("2026 여름");
        season.activateSeason();                                       // status=ACTIVE, startDate=today
        season.updateSeason(null, LocalDate.of(2026, 9, 20));          // eventDate for D-Day countdown
        seasonRepository.save(season);

        // Assign roles
        link(leader,   season, Set.of(UserRole.LEADER));
        link(producer, season, Set.of(UserRole.PRODUCER));
        link(actor1,   season, Set.of(UserRole.ACTOR));
        link(actor2,   season, Set.of(UserRole.ACTOR));
        link(stageDes, season, Set.of(UserRole.STAGE_DESIGN));
        link(soundOp,  season, Set.of(UserRole.SOUND_OPERATOR));
    }

    private User saveMember(String name, String studentNumber, String phoneNumber) {
        return userRepository.save(User.builder()
                .name(name)
                .studentNumber(studentNumber)
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(studentNumber))
                .isAdmin(false)
                .build());
    }

    private void link(User user, Season season, Set<UserRole> roles) {
        userSeasonRepository.save(UserSeason.builder()
                .user(user)
                .season(season)
                .userRoles(roles)
                .build());
    }
}
