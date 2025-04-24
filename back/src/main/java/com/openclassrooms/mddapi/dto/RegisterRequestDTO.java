package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class RegisterRequestDTO {
    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    @NotBlank
    private String password;
}
