package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.*;
import com.openclassrooms.mddapi.model.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

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

    public UserDto userDTO(UserDetails user){
        return UserDto.builder()
                .email(user.getUsername())
                .username(user.getUsername())
                .build();
    }

}
