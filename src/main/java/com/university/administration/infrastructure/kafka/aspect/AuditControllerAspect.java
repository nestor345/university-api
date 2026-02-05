package com.university.administration.infrastructure.kafka.aspect;

import com.university.administration.infrastructure.kafka.AuditPublisher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditControllerAspect {

    private final AuditPublisher auditPublisher;
    private final HttpServletRequest request;

    @Around("execution(* com.university.administration.api.controllers..*(..))")
    public Object auditAllControllers(ProceedingJoinPoint joinPoint) throws Throwable {

        String controllerMethod = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        try {

            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("controllerMethod", controllerMethod);
            metadata.put("durationMs", duration);

            auditPublisher.publish(
                    request,
                    "CONTROLLER_EXECUTION",
                    "SUCCESS",
                    metadata
            );

            return result;

        } catch (Throwable ex) {

            long duration = System.currentTimeMillis() - start;

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("controllerMethod", controllerMethod);
            metadata.put("error", ex.getClass().getSimpleName());
            metadata.put("message", ex.getMessage());
            metadata.put("durationMs", duration);

            auditPublisher.publish(
                    request,
                    "CONTROLLER_EXECUTION",
                    "FAIL",
                    metadata
            );

            throw ex;
        }
    }
}

