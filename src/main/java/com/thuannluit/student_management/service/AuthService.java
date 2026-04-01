package com.thuannluit.student_management.service;

import com.thuannluit.student_management.dto.reponse.ChangePasswordResponse;
import com.thuannluit.student_management.dto.reponse.LoginResponse;
import com.thuannluit.student_management.dto.request.ChangePasswordRequest;
import com.thuannluit.student_management.dto.request.LoginRequest;
import com.thuannluit.student_management.dto.request.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest request);
    String registerAdmin(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    ChangePasswordResponse changePassword(ChangePasswordRequest request);
}

