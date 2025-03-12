package com.beautystore.adeline.services;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.beautystore.adeline.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<com.beautystore.adeline.models.User> userOptional = userRepository.findByEmail(email);
        com.beautystore.adeline.models.User user = userOptional.orElseThrow(() -> {
            logger.error("User not found with email: {}", email);
            return new UsernameNotFoundException("User not found with email: " + email);
        });

        if (user.getRole() == null) {
            logger.error("User role is null for email: {}", email);
            throw new IllegalStateException("User role is null for email: " + email);
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            logger.error("User password is null or empty for email: {}", email);
            throw new IllegalStateException("User password is null or empty for email: " + email);
        }

        // Tạo danh sách authorities từ role của user
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        // Trả về UserDetails của Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}