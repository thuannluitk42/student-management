package com.thuannluit.student_management.service.impl;

import com.thuannluit.student_management.entity.Users;
import com.thuannluit.student_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email: {}", email);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found for email: {}", email);
                    return new UsernameNotFoundException("user.not.found"); // dùng key
                });

        log.debug("Successfully loaded user details for email: {}", email);
        return mapToUserDetails(user);
    }

    private UserDetails mapToUserDetails(Users user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                        .toList()
        );
    }

    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public Users saveUser(Users user) {
        log.info("Saving user details for email: {}", user.getEmail());
        return userRepository.save(user);
    }
}
