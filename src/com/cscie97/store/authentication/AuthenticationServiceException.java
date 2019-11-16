package com.cscie97.store.authentication;

public class AuthenticationServiceException extends Exception {
    private String reason;
    private String fix;

    public AuthenticationServiceException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public String getFix() {
        return fix;
    }
}
