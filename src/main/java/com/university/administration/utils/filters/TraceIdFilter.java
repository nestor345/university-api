package com.university.administration.utils.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

    @Value("${university.servlet.trace-id-header-name:X-Transaction-Id}")
    private String traceHeaderName;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            String transactionId = resolveTransactionId(request);

            MDC.put("transactionId", transactionId);
            MDC.put("startTime", String.valueOf(startTime));

            response.setHeader(traceHeaderName, transactionId);

            log.debug("TransactionId [{}] assigned to request [{} {}]",
                    transactionId,
                    request.getMethod(),
                    request.getRequestURI());

            filterChain.doFilter(request, response);

        } finally {
            MDC.clear();
        }
    }

    private String resolveTransactionId(HttpServletRequest request) {
        String header = request.getHeader(traceHeaderName);
        return (header != null && !header.isBlank())
                ? header
                : UUID.randomUUID().toString();
    }
}

