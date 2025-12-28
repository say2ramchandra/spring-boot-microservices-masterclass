package com.masterclass.context.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserCreatedEvent extends ApplicationEvent {
    private final String username;

    public UserCreatedEvent(Object source, String username) {
        super(source);
        this.username = username;
    }
}
