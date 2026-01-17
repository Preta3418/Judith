package com.judtih.judith_management_system.domain.user.entity;

import com.judtih.judith_management_system.domain.season.Season;
import com.judtih.judith_management_system.domain.user.UserRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints =  @UniqueConstraint(columnNames = {"user_id", "season_id"}))
@Getter
@NoArgsConstructor
public class UserSeason {

    @Builder
    public UserSeason(User user, Season season, Set<UserRole> userRoles) {
        this.user = user;
        this.season = season;
        this.userRoles = userRoles;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ElementCollection
    @CollectionTable(name = "user_season_roles", joinColumns = @JoinColumn(name = "user_season_id"))
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Set<UserRole> userRoles = new HashSet<>();

    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }

    public void updateRoles(Set<UserRole> UserRoles) {
        this.userRoles = UserRoles;
    }

}
