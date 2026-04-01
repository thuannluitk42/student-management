package com.thuannluit.student_management.service.impl;

import com.thuannluit.student_management.dto.reponse.ChangePasswordResponse;
import com.thuannluit.student_management.dto.reponse.LoginResponse;
import com.thuannluit.student_management.dto.request.ChangePasswordRequest;
import com.thuannluit.student_management.dto.request.LoginRequest;
import com.thuannluit.student_management.dto.request.RegisterRequest;
import com.thuannluit.student_management.entity.Roles;
import com.thuannluit.student_management.entity.Users;
import com.thuannluit.student_management.exception.ResourceNotFoundException;
import com.thuannluit.student_management.repository.RoleRepository;
import com.thuannluit.student_management.repository.UserRepository;
import com.thuannluit.student_management.security.JwtUtil;
import com.thuannluit.student_management.service.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    UserRepository authRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerUserDetailsService userDetailsService;

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        boolean isExist = userDetailsService.userExists(request.email());

        Roles roleUser = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        Set<Roles> roles = Set.of(roleUser);

        if (!isExist) {
            userDetailsService.saveUser(new Users(
                    request.email(),
                    passwordEncoder.encode(request.password()),
                    roles)
            );
        }
        return "User registered successfully";
    }

    @Override
    @Transactional
    public String registerAdmin(RegisterRequest request) {
        boolean isExist = userDetailsService.userExists(request.email());

        Roles roleUser = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));

        Set<Roles> roles = Set.of(roleUser);

        if (!isExist) {
            userDetailsService.saveUser(new Users(
                    request.email(),
                    passwordEncoder.encode(request.password()),
                    roles)
            );
        }
        return "Admin registered successfully";
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assert userDetails != null;
        String token = jwtUtil.generateToken(userDetails);
        return new LoginResponse(token, userDetails.getUsername());
    }

    @Override
    @Transactional
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        Users existingUser = authRepository.findById(Integer.valueOf(request.id()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.id()));

        existingUser.setPassword(passwordEncoder.encode(request.newPassword()));

        authRepository.save(existingUser);

        return new ChangePasswordResponse("Password changed successfully");
    }
}
