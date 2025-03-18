package com.openclassrooms.mddapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class RegisterResponseDTO {
    private Long userId;
    private String username;
    private String email;
    private String token;
}
