package com.judtih.judith_management_system.domain.graduate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "graduates")
public class Graduate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String studentNumber;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "graduated_at", nullable = false)
    private LocalDateTime graduatedAt;

    @PrePersist
    protected void onCreate() {
        graduatedAt = LocalDateTime.now();
    }

}
