package com.university.administration.utils.logs;

import java.util.Map;

import com.university.administration.domain.logs.CallResource;
import com.university.administration.domain.logs.ResultTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class RepositoryLoggingAspect {

    @Around("execution(* com.university.administration..repository..*(..))")
    public Object logRepositoryCall(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        ResultTransaction result = ResultTransaction.SUCCESS;
        Map<String, Object> additionalInfo = null;

        String service = "DB " + pjp.getSignature().getName();

        try {
            return pjp.proceed();

        } catch (Exception e) {
            log.error(e.getMessage());
            result = ResultTransaction.FAIL;
            additionalInfo = Map.of("error", e.getMessage());
            throw e;

        } finally {
            APILogger.writeCallLog(service, CallResource.BD_POS, result, start, additionalInfo);
        }
    }

}
