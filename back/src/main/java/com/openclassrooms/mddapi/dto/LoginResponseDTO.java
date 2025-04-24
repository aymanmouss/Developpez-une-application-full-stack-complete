package com.openclassrooms.mddapi.dto;

import com.openclassrooms.mddapi.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String token;
    private Date expiryDate;
    private User user;
}
