package com.ecommerce.order.client;

import com.ecommerce.order.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client to communicate with User Service
 */
@Component
public class UserServiceClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${user-service.url}")
    private String userServiceUrl;
    
    public UserDTO getUserById(Long userId) {
        String url = userServiceUrl + "/api/users/" + userId;
        try {
            return restTemplate.getForObject(url, UserDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user with id: " + userId, e);
        }
    }
}
