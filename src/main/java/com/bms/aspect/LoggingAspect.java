package com.bms.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.bms.service..*(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            log.info("Executing: {} with args: {}",
                    joinPoint.getSignature(),
                    Arrays.toString(joinPoint.getArgs()));

            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            log.info("{} executed in {} ms",
                    joinPoint.getSignature(),
                    executionTime);
            log.info("Return: {}", result);

            return result;
        } catch (Exception e) {
            log.error("Exception in {}: {}",
                    joinPoint.getSignature(),
                    e.getMessage());
            throw e;
        }
    }
}
