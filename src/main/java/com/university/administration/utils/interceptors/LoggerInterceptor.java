package com.university.administration.utils.interceptors;

import com.university.administration.utils.logs.APILogger;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        log.debug("Received request: Type: [{}] in path: [{}] from [{}]", request.getMethod(), request.getRequestURI(),
                request.getRemoteAddr());

        if (!request.getRequestURI().contains("actuator") && !request.getRequestURI().contains("/favicon.ico")) {
            MDC.put("startTime", Long.toString(System.currentTimeMillis()));

            APILogger.writeInputLog(request.getMethod().toUpperCase() + " " + request.getRequestURI(), null);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        log.debug("Sent response: Type: [{}] in path: [{}] with status: [{}]", request.getMethod(),
                request.getRequestURI(), response.getStatus());
    }
}
