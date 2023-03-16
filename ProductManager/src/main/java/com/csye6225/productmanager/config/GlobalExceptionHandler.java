package com.csye6225.productmanager.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateSkuException.class)
    public ResponseEntity<String> handleDuplicateSkuException(DuplicateSkuException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @Component
    public static class AuthenticationFacade implements DuplicateSkuException.IAuthenticationFacade {

        @Override
        public Authentication getAuthentication() {
            return SecurityContextHolder.getContext().getAuthentication();
        }
    }
}