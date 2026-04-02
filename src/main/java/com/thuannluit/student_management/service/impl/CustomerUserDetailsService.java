package com.thuannluit.student_management.service.impl;

import com.thuannluit.student_management.entity.Users;
import com.thuannluit.student_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("user.not.found") // dùng key
                );

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
        return userRepository.save(user);
    }
}