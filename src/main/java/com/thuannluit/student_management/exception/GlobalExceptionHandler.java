package com.thuannluit.student_management.exception;

import com.thuannluit.student_management.dto.reponse.ErrorResponse;
import com.thuannluit.student_management.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    MessageService messageService;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        log.warn("AppException occurred: {}", ex.getMessage());
        
        String message = messageService.get(ex.getErrorCode());

        return buildError(
                ex.getErrorCode(),
                message,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn("ResourceNotFoundException occurred: {}", ex.getMessage());

        return buildError(
                "resource.not.found",
                messageService.get("resource.not.found"),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("AccessDeniedException occurred: {}", ex.getMessage());

        return buildError(
                "access.denied",
                messageService.get("access.denied"),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String messageKey = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("validation.error");

        log.warn("Validation error occurred: {}", messageKey);
        
        String localizedMessage = messageService.get(messageKey);

        return buildError(
                "validation.error",
                localizedMessage,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(Exception ex) {
        log.error("Unhandled Exception caught: ", ex);

        return buildError(
                "internal.server.error",
                messageService.get("internal.server.error"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private ResponseEntity<ErrorResponse> buildError(String code, String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(code, message));
    }
}
