package com.example.stock.security;

import com.example.stock.entity.User;
import com.example.stock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String roleName = user.getRole() != null ? user.getRole().name() : "ROLE_USER";

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword() != null ? user.getPassword() : "",
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}
