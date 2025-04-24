package com.openclassrooms.mddapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Table(name = "USERS")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User implements  UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password; // Return the password field
    }

    @Override
    public String getUsername() {
        return username; // Return the username field
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Or your implementation
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Or your implementation
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Or your implementation
    }

    @Override
    public boolean isEnabled() {
        return true; // Or your implementation
    }
}
