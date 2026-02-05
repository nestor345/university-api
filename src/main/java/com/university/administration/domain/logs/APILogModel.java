package com.university.administration.domain.logs;

import java.time.OffsetDateTime;
import java.util.Map;

import org.slf4j.MDC;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APILogModel {

    private String origin = "university-administration";
    private LogCategory logCategory;
    private String timestamp = OffsetDateTime.now().toString();
    private String transactionId = MDC.get("transactionId");
    private String internalId = MDC.get("internalId");
    private String service;
    private ActivityPhase activity;
    private CallResource callResource;
    private ResultTransaction result;
    private double duration;
    private Map<String, Object> aditionalInfo;

}
