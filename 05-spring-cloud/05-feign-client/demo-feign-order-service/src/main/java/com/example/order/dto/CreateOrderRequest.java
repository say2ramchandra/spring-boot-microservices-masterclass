package com.example.order.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create Order Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity cannot exceed 100")
    private Integer quantity;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Customer email is required")
    private String customerEmail;
}
