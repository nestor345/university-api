package com.university.administration.infrastructure.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.administration.infrastructure.kafka.model.AuditEvent;
import com.university.administration.infrastructure.persistence.entity.AuditLog;
import com.university.administration.infrastructure.persistence.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuditConsumerTest {

    private AuditLogRepository repository;
    private ObjectMapper objectMapper;
    private AuditConsumer consumer;

    @BeforeEach
    void setUp() {
        repository = mock(AuditLogRepository.class);
        objectMapper = mock(ObjectMapper.class);
        consumer = new AuditConsumer(repository, objectMapper);
    }

    @Test
    void shouldConsumeAndSaveAuditLog() {

        AuditEvent event = AuditEvent.builder()
                .transactionId("tx123")
                .method("POST")
                .endpoint("/api/test")
                .action("CREATE")
                .status("SUCCESS")
                .metadata(Map.of("key", "value"))
                .timestamp(Instant.now())
                .build();

        consumer.consume(event);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(repository).save(captor.capture());

        AuditLog saved = captor.getValue();

        assertThat(saved.getTransactionId()).isEqualTo("tx123");
        assertThat(saved.getMethod()).isEqualTo("POST");
        assertThat(saved.getEndpoint()).isEqualTo("/api/test");
        assertThat(saved.getAction()).isEqualTo("CREATE");
        assertThat(saved.getStatus()).isEqualTo("SUCCESS");
        assertThat(saved.getMetadata()).isEqualTo(event.getMetadata());
        assertThat(saved.getTimestamp()).isEqualTo(event.getTimestamp());
    }

    @Test
    void shouldWriteMetadataSuccessfully() throws Exception {

        AuditEvent event = AuditEvent.builder()
                .metadata(Map.of("a", 1))
                .build();

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"a\":1}");

        Method method = AuditConsumer.class
                .getDeclaredMethod("writeMetadata", AuditEvent.class);

        method.setAccessible(true);

        String result = (String) method.invoke(consumer, event);

        assertThat(result).isEqualTo("{\"a\":1}");
    }

    @Test
    void shouldReturnEmptyJsonWhenMetadataSerializationFails() throws Exception {

        AuditEvent event = AuditEvent.builder()
                .metadata(Map.of("a", 1))
                .build();

        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new RuntimeException("boom"));

        Method method = AuditConsumer.class
                .getDeclaredMethod("writeMetadata", AuditEvent.class);

        method.setAccessible(true);

        String result = (String) method.invoke(consumer, event);

        assertThat(result).isEqualTo("{}");
    }
}
