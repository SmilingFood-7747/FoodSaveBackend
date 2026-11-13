package com.foodsave.platform.shared.interfaces.rest;

public class ResourceNotFoundException extends RuntimeException {
    private final String messageKey;

    public ResourceNotFoundException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public String messageKey() { return messageKey; }
}
