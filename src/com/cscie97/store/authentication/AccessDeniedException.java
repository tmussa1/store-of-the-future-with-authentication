package com.cscie97.store.authentication;

public class AccessDeniedException extends AuthenticationServiceException {

    public AccessDeniedException(String message, String reason) {
        super(message, reason);
    }
}
