package com.masterclass.context.service;

import com.masterclass.context.event.UserCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class UserService {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void createUser(String username) {
        System.out.println("   Creating user: " + username);
        
        // Simulate user creation
        // ...
        
        // Publish event
        eventPublisher.publishEvent(new UserCreatedEvent(this, username));
        
        System.out.println("   User created successfully: " + username);
    }
}
