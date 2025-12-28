package com.masterclass.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    @Around("execution(* com.masterclass.aop.service.UserService.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        System.out.println("   [Performance] ⏱️  Starting timer for: " + 
            joinPoint.getSignature().getName() + "()");
        
        Object result = joinPoint.proceed(); // Execute actual method
        
        long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("   [Performance] ⏱️  Execution time: " + executionTime + "ms");
        
        if (executionTime > 100) {
            System.out.println("   [Performance] ⚠️  Slow method detected!");
        }
        
        return result;
    }
}
