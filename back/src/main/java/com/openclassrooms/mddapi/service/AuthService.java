package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.LoginRequestDTO;
import com.openclassrooms.mddapi.dto.RegisterRequestDTO;
import com.openclassrooms.mddapi.dto.RegisterResponseDTO;
import com.openclassrooms.mddapi.mapper.AuthMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final JwtTokenUtil jwtTokenUtil;
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

            String token = jwtTokenUtil.generateToken(userDetails);

            return authMapper.toDto(user,token);
        }catch (Exception e){
            throw new BadCredentialsException("Registration failed: " + e);
        }

    }

    public String login(LoginRequestDTO dto){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsernameOrEmail(), dto.getPassword()));
            final UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getUsernameOrEmail());

            return jwtTokenUtil.generateToken(userDetails);
        }catch (BadCredentialsException e){
            throw new BadCredentialsException("Invalid email or password");
        }
    }

}
