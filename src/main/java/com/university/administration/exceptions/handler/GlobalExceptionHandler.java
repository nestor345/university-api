package com.university.administration.exceptions.handler;

import com.university.administration.domain.logs.ResultTransaction;
import com.university.administration.exceptions.BusinessException;
import com.university.administration.exceptions.model.ApiError;
import com.university.administration.utils.logs.APILogger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final String START_TIME_KEY = "startTime";

    private static final String TRANSACTION_ID_KEY = "transactionId";


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        String detail = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                detail,
                request
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.UNAUTHORIZED,
                "Authentication Failed",
                "Invalid username or password",
                request
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.UNAUTHORIZED,
                "Bad Credentials",
                "Username or password is incorrect",
                request
        );
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.FORBIDDEN,
                "Access Denied",
                "You do not have permission to access this resource",
                request
        );
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON",
                "Request body is invalid or unreadable",
                request
        );
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleBusinessConflict(
            IllegalStateException ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.CONFLICT,
                "Business Rule Violation",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        log.warn("Business error: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .type(request.getRequestURL().toString())
                .title("Business Rule Violation")
                .status(HttpStatus.CONFLICT.value())
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .transactionId(MDC.get("transactionId"))
                .timestamp(Instant.now())
                .build();

        APILogger.writeOutputLog(request.getMethod().toUpperCase() + " " + request.getRequestURI(),
                ResultTransaction.FAIL, Long.parseLong(MDC.get(START_TIME_KEY)), null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }



    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            Exception ex,
            HttpServletRequest request
    ) {
        APILogger.writeOutputLog(request.getMethod().toUpperCase() + " " + request.getRequestURI(),
                ResultTransaction.FAIL, Long.parseLong(MDC.get(START_TIME_KEY)), null);

        return buildError(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage(),
                request
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error("Unexpected error occurred", ex);
        APILogger.writeOutputLog(request.getMethod().toUpperCase() + " " + request.getRequestURI(),
                ResultTransaction.FAIL, Long.parseLong(MDC.get(START_TIME_KEY)), null);

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Unexpected error occurred while processing the request",
                request
        );
    }


    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String title,
            String detail,
            HttpServletRequest request
    ) {

        String transactionId = MDC.get(TRANSACTION_ID_KEY);

        ApiError error = ApiError.builder()
                .type(request.getRequestURL().toString())
                .title(title)
                .status(status.value())
                .detail(detail)
                .instance(request.getRequestURI())
                .transactionId(transactionId)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    private String formatFieldError(FieldError error) {
        return "[" + error.getField() + "] " + error.getDefaultMessage();
    }
}
