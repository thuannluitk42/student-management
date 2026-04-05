package com.thuannluit.student_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thuannluit.student_management.controller.AuthController;
import com.thuannluit.student_management.dto.reponse.LoginResponse;
import com.thuannluit.student_management.dto.reponse.ChangePasswordResponse;
import com.thuannluit.student_management.dto.request.LoginRequest;
import com.thuannluit.student_management.dto.request.RegisterRequest;
import com.thuannluit.student_management.dto.request.ChangePasswordRequest;
import com.thuannluit.student_management.exception.AppException;
import com.thuannluit.student_management.exception.GlobalExceptionHandler;
import com.thuannluit.student_management.service.MessageService;
import com.thuannluit.student_management.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
class AuthControllerDocsTest {

    private MockMvc mockMvc;
    private AuthService authService;
    private MessageService messageService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        authService = mock(AuthService.class);
        messageService = mock(MessageService.class);
        when(messageService.get(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        AuthController controller = new AuthController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "authService", authService);

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        org.springframework.test.util.ReflectionTestUtils.setField(exceptionHandler, "messageService", messageService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void register_shouldGenerateDocs() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn("User registered successfully");

        RegisterRequest req = new RegisterRequest("user@example.com", "123456");

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(document("auth/register",
                        requestFields(
                                fieldWithPath("email").description("User email address"),
                                fieldWithPath("password").description("User password (min 6 characters)")
                        ),
                        responseBody()
                ));
    }

    @Test
    void registerAdmin_shouldGenerateDocs() throws Exception {
        when(authService.registerAdmin(any(RegisterRequest.class))).thenReturn("Admin registered successfully");

        RegisterRequest req = new RegisterRequest("admin@example.com", "123456");

        mockMvc.perform(post("/v1/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(document("auth/register-admin",
                        requestFields(
                                fieldWithPath("email").description("Admin email address"),
                                fieldWithPath("password").description("Admin password (min 6 characters)")
                        ),
                        responseBody()
                ));
    }

    @Test
    void login_shouldGenerateDocs() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", "user@example.com"));

        LoginRequest req = new LoginRequest("user@example.com", "123456");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(document("auth/login",
                        requestFields(
                                fieldWithPath("email").description("User email address"),
                                fieldWithPath("password").description("User password")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("JWT access token"),
                                fieldWithPath("email").description("User email")
                        )
                ));
    }

    @Test
    void changePassword_shouldGenerateDocs() throws Exception {
        when(authService.changePassword(any(ChangePasswordRequest.class)))
                .thenReturn(new ChangePasswordResponse("Password changed successfully"));

        ChangePasswordRequest req = new ChangePasswordRequest("1", "oldPassword123", "newPassword123");

        mockMvc.perform(post("/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(document("auth/change-password",
                        requestFields(
                                fieldWithPath("id").description("User ID"),
                                fieldWithPath("currentPassword").description("Current password"),
                                fieldWithPath("newPassword").description("New password (min 6 characters)")
                        ),
                        responseFields(
                                fieldWithPath("message").description("Result message")
                        )
                ));
    }

    @Test
    void login_invalidCredentials_shouldGenerateErrorDocs() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new AppException("auth.login.fail", HttpStatus.UNAUTHORIZED));

        LoginRequest req = new LoginRequest("user@example.com", "wrongpassword");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andDo(document("auth/login-401",
                        requestFields(
                                fieldWithPath("email").description("User email address"),
                                fieldWithPath("password").description("User password")
                        ),
                        responseFields(
                                fieldWithPath("errorCode").description("Error code"),
                                fieldWithPath("message").description("Error message"),
                                fieldWithPath("timestamp").description("Timestamp when error occurred")
                        )
                ));
    }
}