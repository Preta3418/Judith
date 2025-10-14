package com.judtih.judith_management_system.domain.graduate;

import jakarta.persistence.*;

@Entity
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

}
