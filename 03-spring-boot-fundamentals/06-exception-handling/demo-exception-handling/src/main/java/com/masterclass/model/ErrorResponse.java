package com.masterclass.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard error response structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> details;
    
    public ErrorResponse(int status, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = getErrorName(status);
        this.message = message;
        this.path = path.replace("uri=", "");
        this.details = new ArrayList<>();
    }
    
    public ErrorResponse(int status, String message, String path, List<String> details) {
        this(status, message, path);
        this.details = details != null ? details : new ArrayList<>();
    }
    
    private String getErrorName(int status) {
        return switch (status) {
            case 400 -> "BAD_REQUEST";
            case 401 -> "UNAUTHORIZED";
            case 403 -> "FORBIDDEN";
            case 404 -> "NOT_FOUND";
            case 409 -> "CONFLICT";
            case 500 -> "INTERNAL_SERVER_ERROR";
            default -> "ERROR";
        };
    }
}
