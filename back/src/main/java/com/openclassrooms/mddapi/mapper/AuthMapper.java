package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.LoginRequestDTO;
import com.openclassrooms.mddapi.dto.RegisterRequestDTO;
import com.openclassrooms.mddapi.dto.RegisterResponseDTO;
import com.openclassrooms.mddapi.model.User;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthMapper {

    public User toEntity(RegisterRequestDTO dto){
        return User.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .build();
    }

    public RegisterResponseDTO toDto(User user, String token){
        return RegisterResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build();
    }

}
