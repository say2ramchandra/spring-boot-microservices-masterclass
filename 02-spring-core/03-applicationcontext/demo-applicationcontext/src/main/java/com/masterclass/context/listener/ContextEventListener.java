package com.masterclass.context.listener;

import org.springframework.context.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ContextEventListener {

    @EventListener
    public void handleContextRefreshed(ContextRefreshedEvent event) {
        System.out.println("   [Event] Context refreshed");
    }

    @EventListener
    public void handleContextStarted(ContextStartedEvent event) {
        System.out.println("   [Event] Context started");
    }

    @EventListener
    public void handleContextStopped(ContextStoppedEvent event) {
        System.out.println("   [Event] Context stopped");
    }

    @EventListener
    public void handleContextClosed(ContextClosedEvent event) {
        System.out.println("   [Event] Context closed");
    }
}
