package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.*;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final AuthService authService;
    private final UserRepository userRepository;


   @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto){
       RegisterResponseDTO register = authService.register(dto);
       return ResponseEntity.ok(register);

   }
   @PostMapping("/login")
    public ResponseEntity<RegisterResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto){
       RegisterResponseDTO loginResponse = authService.login(dto);
       return ResponseEntity.ok(loginResponse);
   }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUserInfo() {
        UserDto userDto = authService.me();
        return ResponseEntity.ok(userDto);
    }
}