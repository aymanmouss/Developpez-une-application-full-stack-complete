package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.*;
import com.openclassrooms.mddapi.mapper.AuthMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * Service class responsible for authentication and user-related operations.
 * Handles registration, login, user info retrieval, and profile updates.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user.
     *
     * @param dto the user registration data
     * @return a response containing the new user's details and JWT token
     * @throws RuntimeException if the email or username already exists
     * @throws BadCredentialsException if registration fails
     */
    public RegisterResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        try {
            User user = authMapper.toEntity(dto);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);

            final UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());
            String token = jwtService.generateToken(userDetails);

            return authMapper.toDto(user, token, jwtService.extractExpiration(token));
        } catch (Exception e) {
            throw new BadCredentialsException("Registration failed: " + e);
        }
    }

    /**
     * Authenticates a user and returns their details with a JWT token.
     *
     * @param dto the login credentials (email or username, and password)
     * @return the authenticated user's details and token
     * @throws BadCredentialsException if authentication fails
     */
    public RegisterResponseDTO login(LoginRequestDTO dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsernameOrEmail(), dto.getPassword()));

            final UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getUsernameOrEmail());
            String jwt = jwtService.generateToken(userDetails);

            User user = userRepository.findByUsername(dto.getUsernameOrEmail())
                    .orElseGet(() -> userRepository.findByEmail(dto.getUsernameOrEmail())
                            .orElseThrow(() -> new BadCredentialsException("User not found")));

            return authMapper.toDto(user, jwt, jwtService.extractExpiration(jwt));

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the authenticated user entity
     * @throws RuntimeException if the user is not found
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Retrieves basic information about the currently authenticated user.
     *
     * @return a user DTO with public profile details
     */
    public UserDto me() {
        return authMapper.userDTO(getCurrentUser());
    }

    /**
     * Updates the current user's profile information and returns a new JWT token.
     *
     * @param dto the updated user details
     * @return a map containing the new JWT token
     */
    public Map<String, String> userUpdate(UpdateUserDto dto) {
        User currentUser = getCurrentUser();
        User user = authMapper.updateUser(currentUser, dto, passwordEncoder);
        userRepository.save(user);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}