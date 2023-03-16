package com.csye6225.productmanager.config;

import org.springframework.security.core.Authentication;

public class DuplicateSkuException extends RuntimeException {
    public DuplicateSkuException(String message) {
        super(message);
    }

    public interface IAuthenticationFacade {
        Authentication getAuthentication();
    }
}