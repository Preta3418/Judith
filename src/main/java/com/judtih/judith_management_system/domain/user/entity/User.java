package com.judtih.judith_management_system.domain.user.entity;

import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Builder
    public User(String name, String studentNumber, String phoneNumber, boolean isAdmin) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "student_number", unique = true)
    private String studentNumber;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean isAdmin ;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "graduated_at")
    private LocalDateTime graduatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void updateInfo(String name, String phoneNumber) {
        if (name != null) this.name = name;
        if (phoneNumber != null) this.phoneNumber = phoneNumber.replaceAll("-", "");
    }

    public void graduate() {
        this.status = UserStatus.GRADUATED;
        this.graduatedAt = LocalDateTime.now();
    }

    public void reactivate() {
        this.status = UserStatus.ACTIVE;
        this.graduatedAt = null;
    }
}


