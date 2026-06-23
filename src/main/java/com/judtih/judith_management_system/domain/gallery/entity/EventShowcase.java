package com.judtih.judith_management_system.domain.gallery.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Placeholder entity for a public showcase of a past production; not yet fully implemented. */
@Entity
@Getter
@NoArgsConstructor
public class EventShowcase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

}
