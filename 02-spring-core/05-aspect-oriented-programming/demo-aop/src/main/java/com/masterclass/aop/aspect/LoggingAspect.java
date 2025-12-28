package com.masterclass.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.masterclass.aop.service.UserService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("   [Logging] → Method called: " + joinPoint.getSignature().getName() +
                "() with args: " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(
        pointcut = "execution(* com.masterclass.aop.service.UserService.get*(..))",
        returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("   [Logging] ← Method returned: " + joinPoint.getSignature().getName() +
                "() => " + result);
    }

    @After("execution(* com.masterclass.aop.service.UserService.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("   [Logging] ✓ Method completed: " + joinPoint.getSignature().getName() + "()");
    }

    @AfterThrowing(
        pointcut = "execution(* com.masterclass.aop.service.UserService.*(..))",
        throwing = "exception"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        System.out.println("   [Logging] ✗ Exception in " + joinPoint.getSignature().getName() +
                "(): " + exception.getMessage());
    }
}
