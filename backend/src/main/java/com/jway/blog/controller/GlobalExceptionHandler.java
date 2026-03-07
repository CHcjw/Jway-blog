package com.jway.blog.controller;

import com.jway.blog.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus
    public ApiResponse<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus code = HttpStatus.valueOf(ex.getStatusCode().value());
        log.warn("Handled response status exception: status={}, reason={}", code.value(), ex.getReason());
        return new ApiResponse<>(code.value(), ex.getReason() == null ? code.getReasonPhrase() : ex.getReason(), null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Map<String, Object>> handleException(Exception ex) {
        log.error("Unhandled exception captured by GlobalExceptionHandler", ex);
        return new ApiResponse<>(500, "internal server error", null);
    }
}
