package com.masterclass.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Validation error response structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
    
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;
    
    public ValidationErrorResponse(String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = 400;
        this.error = "VALIDATION_ERROR";
        this.message = message;
        this.path = path.replace("uri=", "");
        this.fieldErrors = new HashMap<>();
    }
    
    public void addFieldError(String field, String message) {
        this.fieldErrors.put(field, message);
    }
}
