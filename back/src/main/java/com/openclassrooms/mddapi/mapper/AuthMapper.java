package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.*;
import com.openclassrooms.mddapi.model.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@Configuration
public class AuthMapper {

    public User toEntity(RegisterRequestDTO dto){
        return User.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .build();
    }

    public RegisterResponseDTO toDto(User user, String token, Date expiryDate){
        return RegisterResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .exipryDate(expiryDate)
                .build();
    }

    public UserDto userDTO(User user){
        return UserDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    public User updateUser(User user, UpdateUserDto dto, PasswordEncoder passwordEncoder){
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            user.setUsername(dto.getUsername());
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(dto.getPassword());
            user.setPassword(encodedPassword);
        }

        return user;
    }

}
