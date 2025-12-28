package com.masterclass.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * MDC Filter
 * Adds requestId to MDC for all logs in the request
 */
@Component
@Order(1)
@Slf4j
public class MdcFilter implements Filter {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        try {
            // Generate and add request ID
            String requestId = UUID.randomUUID().toString();
            MDC.put(REQUEST_ID, requestId);
            
            // Add user ID (simulated - in real app, get from SecurityContext)
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String userId = httpRequest.getHeader("X-User-Id");
            if (userId != null) {
                MDC.put(USER_ID, userId);
            } else {
                MDC.put(USER_ID, "anonymous");
            }
            
            log.debug("Request started: {} {}", 
                ((HttpServletRequest) request).getMethod(),
                ((HttpServletRequest) request).getRequestURI());
            
            // Continue with the request
            chain.doFilter(request, response);
            
            log.debug("Request completed");
            
        } finally {
            // Always clear MDC to prevent memory leaks
            MDC.clear();
        }
    }
}
