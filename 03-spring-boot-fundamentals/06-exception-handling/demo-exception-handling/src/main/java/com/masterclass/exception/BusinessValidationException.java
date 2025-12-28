package com.masterclass.exception;

import java.util.List;

/**
 * Exception thrown when business validation fails
 */
public class BusinessValidationException extends RuntimeException {
    
    private List<String> errors;
    
    public BusinessValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public BusinessValidationException(String message) {
        super(message);
    }
    
    public List<String> getErrors() {
        return errors;
    }
}
