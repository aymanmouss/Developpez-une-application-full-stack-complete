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


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterResponseDTO register(RegisterRequestDTO dto) {

        if(userRepository.existsByEmail(dto.getEmail()) ){
            throw new RuntimeException("Email already exists");
        }
        if(userRepository.existsByUsername(dto.getUsername()) ){
            throw new RuntimeException("Username already exists");
        }
        try {
            User user = authMapper.toEntity(dto);

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepository.save(user);

            final UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());

            String token = jwtService.generateToken(userDetails);

            return authMapper.toDto(user,token, jwtService.extractExpiration(token));
        }catch (Exception e){
            throw new BadCredentialsException("Registration failed: " + e);
        }
    }

    public RegisterResponseDTO login(LoginRequestDTO dto){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsernameOrEmail(), dto.getPassword()));
            final UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getUsernameOrEmail());
            String jwt = jwtService.generateToken(userDetails);
            User user = userRepository.findByUsername(dto.getUsernameOrEmail())
                    .orElseGet(() -> userRepository.findByEmail(dto.getUsernameOrEmail())
                            .orElseThrow(() -> new BadCredentialsException("User not found")));
            return authMapper.toDto(user, jwt, jwtService.extractExpiration(jwt));

        }catch (BadCredentialsException e){
            throw new BadCredentialsException("Invalid email or password");
        }
    }
    public User getCurrentUser() {
        // Get the authenticated user from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserDto me(){
        return authMapper.userDTO(getCurrentUser());
    }
}
