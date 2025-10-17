package com.judtih.judith_management_system.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    public User() {
    }

    public User(String name, String studentNumber, String phoneNumber, UserRole role) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.phoneNumber = phoneNumber;
        this.role = role;
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
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "graduated_at")
    private LocalDateTime graduatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}


