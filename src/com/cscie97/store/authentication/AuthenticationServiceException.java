package com.cscie97.store.authentication;

public class AuthenticationServiceException extends Exception {
    private String reason;
    private String fix;

    public AuthenticationServiceException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
