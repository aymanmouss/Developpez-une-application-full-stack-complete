package com.openclassrooms.ChatTop.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "rentals", schema = "chatop")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 255)
    private String name;

    private BigDecimal surface;
    private BigDecimal price;

    @Column(length = 255)
    private String picture;

    @Column(length = 2000)
    private String description;

   @ManyToOne
   @JoinColumn(name = "owner_id" , nullable = false)
    private User owner;

    @CreatedDate
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
