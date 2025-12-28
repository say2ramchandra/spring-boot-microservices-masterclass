package com.masterclass.exception;

/**
 * Custom exception thrown when a requested resource is not found.
 * 
 * Extends RuntimeException (unchecked exception).
 * Will be caught by @ControllerAdvice and converted to appropriate HTTP response.
 * 
 * @author Spring Boot Microservices Masterclass
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
