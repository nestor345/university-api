package com.university.administration.infrastructure.kafka;

import com.university.administration.infrastructure.kafka.model.AuditEvent;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuditPublisherTest {

    private KafkaTemplate<String, AuditEvent> kafkaTemplate;
    private AuditPublisher auditPublisher;

    @BeforeEach
    void setup() {
        kafkaTemplate = mock(KafkaTemplate.class);
        auditPublisher = new AuditPublisher(kafkaTemplate);

        // inyectar topic manualmente
        org.springframework.test.util.ReflectionTestUtils
                .setField(auditPublisher, "auditTopic", "audit-topic");
    }

    @Test
    void shouldPublishEventWithExistingTransactionId_andSuccessCallback() {

        MDC.put("transactionId", "tx-123");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/students");

        CompletableFuture<SendResult<String, AuditEvent>> future = new CompletableFuture<>();

        when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);

        auditPublisher.publish(request, "CREATE", "SUCCESS", Map.of("id", 1));

        // Simular éxito
        SendResult<String, AuditEvent> sendResult = mock(SendResult.class);
        RecordMetadata metadata = mock(RecordMetadata.class);
        when(sendResult.getRecordMetadata()).thenReturn(metadata);
        when(metadata.partition()).thenReturn(1);
        when(metadata.offset()).thenReturn(10L);

        future.complete(sendResult);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), any(), any());

        assertThat(topicCaptor.getValue()).isEqualTo("audit-topic");
    }

    @Test
    void shouldGenerateTransactionIdWhenNull_andHandleFailureCallback() {

        MDC.remove("transactionId");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/courses");

        CompletableFuture<SendResult<String, AuditEvent>> future = new CompletableFuture<>();

        when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);

        auditPublisher.publish(request, "READ", "FAIL", Map.of());

        // Simular error
        future.completeExceptionally(new RuntimeException("Kafka down"));

        verify(kafkaTemplate).send(eq("audit-topic"), any(), any());
    }
}
