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
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "rental_id")
    private Rental rental;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 2000)
    private String message;

    @CreatedDate
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
