package com.masterclass.aop.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    // Define reusable pointcut
    @Pointcut("execution(* com.masterclass.aop.service.UserService.delete*(..))")
    public void deleteMethods() {}

    @Pointcut("execution(* com.masterclass.aop.service.UserService.update*(..))")
    public void updateMethods() {}

    // Combine pointcuts with OR
    @Before("deleteMethods() || updateMethods()")
    public void checkSecurity() {
        System.out.println("   [Security] 🔒 Security check passed (simulated)");
    }
}
