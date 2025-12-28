package com.masterclass.context.service;

import com.masterclass.context.event.UserCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationService {

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        System.out.println("   [NotificationService] Sending welcome email to: " + event.getUsername());
    }
}
