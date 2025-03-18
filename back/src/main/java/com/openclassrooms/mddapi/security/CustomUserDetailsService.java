package com.openclassrooms.mddapi.security;

import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CustomUserDetailsService  implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException{

        Optional<User> userByEmail = userRepository.findByEmail(login);

        Optional<User> userByUsername = userRepository.findByUsername(login);

        Optional<User> foundUser = userByEmail.isPresent() ? userByEmail : userByUsername;

        User user = foundUser.orElseThrow(() ->
                new UsernameNotFoundException("User not found with username or email : " + login));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );

    }
}
