package com.openclassrooms.mddapi.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "TOPICS")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;
}
