package com.university.administration.infrastructure.kafka;

import com.university.administration.infrastructure.kafka.model.AuditEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditPublisher {

    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;

    @Value("${university.kafka.topics.audit}")
    private String auditTopic;

    public void publish(HttpServletRequest request,
                        String action,
                        String status,
                        Map<String, Object> metadata) {

        String transactionId = MDC.get("transactionId");

        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
        }

        AuditEvent event = AuditEvent.builder()
                .transactionId(transactionId)
                .method(request.getMethod())
                .endpoint(request.getRequestURI())
                .action(action)
                .status(status)
                .metadata(metadata)
                .timestamp(Instant.now())
                .build();

        String finalTransactionId = transactionId;

        log.info("TRYING TO SEND EVENT to KAFKA: topic={}, txId={}, event={}",
                auditTopic, finalTransactionId, event);

        kafkaTemplate.send(auditTopic, transactionId, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.info("EVENT PUBLISH(FAILED): txId={}", finalTransactionId, ex);
                    } else {
                        log.info("EVENT SENT (SUCCESS): txId={}, topic={}, partition={}, offset={}",
                                finalTransactionId,
                                auditTopic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
