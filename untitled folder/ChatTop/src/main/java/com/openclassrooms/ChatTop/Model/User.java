package com.openclassrooms.ChatTop.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;

    @Column(length = 255)
    private String email;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String password;

    @CreatedDate
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
