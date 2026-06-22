package com.judtih.judith_management_system.domain.user.entity;

import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/** Core user entity; table name is "users" to avoid collision with the reserved SQL keyword USER. */
@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Builder
    public User(String name, String studentNumber, String phoneNumber, String password, boolean isAdmin) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin;
        this.password = password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(name = "student_number", unique = true, nullable = false)
    private String studentNumber;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean passwordChanged = false; // false until user explicitly changes from the default (studentNumber)

    @Column(nullable = false)
    private boolean isAdmin ;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "inactive_since")
    private LocalDateTime inactiveSince; // null for active users; set on deactivate(), cleared on reactivate()


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /** Strips hyphens from phoneNumber so the DB always stores digits only (e.g., 010-1234-5678 → 01012345678). */
    public void updateInfo(String name, String phoneNumber) {
        if (name != null) this.name = name;
        if (phoneNumber != null) this.phoneNumber = phoneNumber.replaceAll("-", "");
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.inactiveSince = LocalDateTime.now();
    }

    public void reactivate() {
        this.status = UserStatus.ACTIVE;
        this.inactiveSince = null;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
        this.passwordChanged = true;
    }
}


