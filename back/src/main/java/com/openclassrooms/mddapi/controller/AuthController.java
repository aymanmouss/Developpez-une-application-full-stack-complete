package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.*;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for handling authentication-related operations such as
 * registration, login, user info retrieval, and profile updates.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final AuthService authService;
    private final UserRepository userRepository;

    /**
     * Registers a new user.
     *
     * @param dto the registration data (email, password, etc.)
     * @return the registered user's details
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto){
        RegisterResponseDTO register = authService.register(dto);
        return ResponseEntity.ok(register);
    }

    /**
     * Authenticates a user and returns authentication info.
     *
     * @param dto the login data (email and password)
     * @return the authenticated user's details including a token
     */
    @PostMapping("/login")
    public ResponseEntity<RegisterResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto){
        RegisterResponseDTO loginResponse = authService.login(dto);
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Retrieves the currently authenticated user's information.
     *
     * @return the current user's profile data
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUserInfo() {
        UserDto userDto = authService.me();
        return ResponseEntity.ok(userDto);
    }

    /**
     * Updates the current user's profile information.
     *
     * @param dto the updated user data
     * @return a success message or error information
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateUser(@RequestBody UpdateUserDto dto) {
        Map<String, String> message = authService.userUpdate(dto);
        return ResponseEntity.ok(message);
    }
}