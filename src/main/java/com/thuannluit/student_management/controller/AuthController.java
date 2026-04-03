package com.thuannluit.student_management.controller;

import com.thuannluit.student_management.dto.request.ChangePasswordRequest;
import com.thuannluit.student_management.dto.request.LoginRequest;
import com.thuannluit.student_management.dto.request.RegisterRequest;
import com.thuannluit.student_management.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Authentication & Authorization APIs")
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(
            summary = "Register user",
            description = "Register a new user account"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Register success"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "Register admin",
            description = "Register a new admin account"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Register admin success"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Admin registration data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    @Operation(
            summary = "Login",
            description = "Authenticate user and return JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login success"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary = "Change password",
            description = "Change password (requires authentication)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Change password request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChangePasswordRequest.class))
            )
            @Valid @RequestBody ChangePasswordRequest request) {

        return ResponseEntity.ok(authService.changePassword(request));
    }
}