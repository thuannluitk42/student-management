package com.thuannluit.student_management.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException {

    @Autowired
    String errorCode;
    @Autowired
    HttpStatus status;

    public AppException(String errorCode, HttpStatus status) {
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
