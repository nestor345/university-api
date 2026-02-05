package com.university.administration.infrastructure.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.administration.infrastructure.kafka.model.AuditEvent;
import com.university.administration.infrastructure.persistence.entity.AuditLog;
import com.university.administration.infrastructure.persistence.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "university.audit.events")
    public void consume(AuditEvent event) {

        log.info("EVENT RECEIVED: {}", event);

        AuditLog entity = AuditLog.builder()
                .transactionId(event.getTransactionId())
                .method(event.getMethod())
                .endpoint(event.getEndpoint())
                .action(event.getAction())
                .status(event.getStatus())
                .metadata(event.getMetadata())
                .timestamp(event.getTimestamp())
                .build();

        auditLogRepository.save(entity);

        log.info("SAVED TO DB");
    }

    private String writeMetadata(AuditEvent event) {
        try {
            return objectMapper.writeValueAsString(event.getMetadata());
        } catch (Exception e) {
            return "{}";
        }
    }
}



