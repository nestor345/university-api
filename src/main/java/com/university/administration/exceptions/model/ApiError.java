package com.university.administration.exceptions.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ApiError {

    private final String type;
    private final String title;
    private final int status;
    private final String detail;
    private final String instance;
    private final String transactionId;
    private final Instant timestamp;
}

