package com.university.administration.infrastructure.kafka.model;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {

    private String transactionId;
    private String method;
    private String endpoint;
    private String action;
    private String status;
    private Map<String, Object> metadata;
    private Instant timestamp;
}


