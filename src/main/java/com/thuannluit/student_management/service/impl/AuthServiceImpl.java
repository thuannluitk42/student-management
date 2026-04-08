package com.thuannluit.student_management.service.impl;

import com.thuannluit.student_management.dto.reponse.ChangePasswordResponse;
import com.thuannluit.student_management.dto.reponse.LoginResponse;
import com.thuannluit.student_management.dto.request.ChangePasswordRequest;
import com.thuannluit.student_management.dto.request.LoginRequest;
import com.thuannluit.student_management.dto.request.RegisterRequest;
import com.thuannluit.student_management.entity.Roles;
import com.thuannluit.student_management.entity.Users;
import com.thuannluit.student_management.event.AuthEvent;
import com.thuannluit.student_management.event.AuthEventProducer;
import com.thuannluit.student_management.exception.AppException;
import com.thuannluit.student_management.exception.ResourceNotFoundException;
import com.thuannluit.student_management.repository.RoleRepository;
import com.thuannluit.student_management.repository.UserRepository;
import com.thuannluit.student_management.security.JwtUtil;
import com.thuannluit.student_management.service.AuthService;
import com.thuannluit.student_management.service.MessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired AuthenticationManager authenticationManager;
    @Autowired JwtUtil jwtUtil;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired CustomerUserDetailsService userDetailsService;
    @Autowired MessageService messageService;
    private final AuthEventProducer authEventProducer;

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.email());

        validateRegister(request);

        if (userDetailsService.userExists(request.email())) {
            log.warn("Registration failed. User already exists for email: {}", request.email());
            throw new AppException("auth.user.exists", HttpStatus.BAD_REQUEST);
        }

        Roles roleUser = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> {
                    log.error("ROLE_USER not found in the database.");
                    return new AppException("role.not.found", HttpStatus.INTERNAL_SERVER_ERROR);
                });

        userDetailsService.saveUser(new Users(
                request.email(),
                passwordEncoder.encode(request.password()),
                Set.of(roleUser)
        ));

        log.info("User registered successfully with email: {}", request.email());

        authEventProducer.sendMessage(AuthEvent.builder()
                .action("USER_REGISTERED")
                .email(request.email())
                .role("ROLE_USER")
                .timestamp(Instant.now())
                .build());

//        emailService.sendWelcomeEmail(request.email(), request.email());

        return messageService.get("auth.register.success");
    }

    @Override
    @Transactional
    public String registerAdmin(RegisterRequest request) {
        log.info("Attempting to register admin user with email: {}", request.email());

        if (userDetailsService.userExists(request.email())) {
            log.warn("Admin registration failed. User already exists for email: {}", request.email());
            throw new AppException("auth.user.exists", HttpStatus.BAD_REQUEST);
        }

        Roles roleAdmin = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseThrow(() -> {
                    log.error("ROLE_ADMIN not found in the database.");
                    return new AppException("role.not.found", HttpStatus.INTERNAL_SERVER_ERROR);
                });

        userDetailsService.saveUser(new Users(
                request.email(),
                passwordEncoder.encode(request.password()),
                Set.of(roleAdmin)
        ));

        log.info("Admin user registered successfully with email: {}", request.email());

        authEventProducer.sendMessage(AuthEvent.builder()
                .action("ADMIN_REGISTERED")
                .email(request.email())
                .role("ROLE_ADMIN")
                .timestamp(Instant.now())
                .build());

        return messageService.get("auth.register.admin.success");
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.email());
        
        validateLogin(request);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            log.info("Login successful for user: {}", request.email());
            return new LoginResponse(token, userDetails.getUsername());

        } catch (Exception ex) {
            log.error("Login failed for user: {}. Error: {}", request.email(), ex.getMessage());
            throw new AppException("auth.login.fail", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        log.info("Attempting to change password for user ID: {}", request.id());
        
        validateChangePassword(request);

        Users user = userRepository.findById(Integer.valueOf(request.id()))
                .orElseThrow(() -> {
                    log.warn("Change password failed. User not found for ID: {}", request.id());
                    return new ResourceNotFoundException("user.not.found");
                });

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user ID: {}", request.id());
        return new ChangePasswordResponse(
                messageService.get("auth.password.changed")
        );
    }


    private void validateRegister(RegisterRequest request) {
        log.debug("Validating register request for email: {}", request.email());
        validateEmail(request.email());
        validatePassword(request.password());
    }

    private void validateLogin(LoginRequest request) {
        log.debug("Validating login request for email: {}", request.email());
        validateEmail(request.email());
        validatePassword(request.password());
    }

    private void validateChangePassword(ChangePasswordRequest request) {
        log.debug("Validating change password request for user ID: {}", request.id());
        if (request.id() == null || request.id().isBlank()) {
            log.warn("Invalid user ID provided for password change.");
            throw new AppException("user.invalid.id", HttpStatus.BAD_REQUEST);
        }
        validatePassword(request.newPassword());
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            log.warn("Email validation failed: email is null or blank.");
            throw new AppException("auth.email.required", HttpStatus.BAD_REQUEST);
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            log.warn("Email validation failed: invalid format for email: {}", email);
            throw new AppException("auth.email.invalid", HttpStatus.BAD_REQUEST);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            log.warn("Password validation failed: password is null or blank.");
            throw new AppException("auth.password.required", HttpStatus.BAD_REQUEST);
        }

        if (password.length() < 6) {
            log.warn("Password validation failed: password is too short.");
            throw new AppException("auth.password.length", HttpStatus.BAD_REQUEST);
        }
    }
}