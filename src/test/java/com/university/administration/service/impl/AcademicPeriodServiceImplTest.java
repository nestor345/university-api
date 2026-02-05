package com.university.administration.service.impl;

import com.university.administration.domain.dto.AcademicPeriodCreateDTO;
import com.university.administration.domain.dto.AcademicPeriodResponseDTO;
import com.university.administration.exceptions.BusinessException;
import com.university.administration.infrastructure.persistence.entity.AcademicPeriod;
import com.university.administration.infrastructure.persistence.repository.AcademicPeriodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcademicPeriodServiceImplTest {

    private AcademicPeriodRepository repository;
    private AcademicPeriodServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(AcademicPeriodRepository.class);
        service = new AcademicPeriodServiceImpl(repository);
    }

    @Test
    void shouldCreateAcademicPeriodSuccessfully() {

        AcademicPeriodCreateDTO dto = new AcademicPeriodCreateDTO(
                "2026-1",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 30)
        );

        AcademicPeriod saved = AcademicPeriod.builder()
                .id(UUID.randomUUID())
                .name(dto.name())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .active(false)
                .build();

        when(repository.save(any())).thenReturn(saved);

        AcademicPeriodResponseDTO response = service.create(dto);

        verify(repository).save(any());

        assertThat(response.name()).isEqualTo("2026-1");
        assertThat(response.active()).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenStartDateAfterEndDate() {

        AcademicPeriodCreateDTO dto = new AcademicPeriodCreateDTO(
                "2026-1",
                LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 1, 1)
        );

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Start date must be before end date");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldActivateAcademicPeriod() {

        UUID id = UUID.randomUUID();

        AcademicPeriod period = AcademicPeriod.builder()
                .id(id)
                .name("2026-1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .active(false)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(period));

        AcademicPeriodResponseDTO response = service.activate(id);

        verify(repository).deactivateAll();
        assertThat(period.getActive()).isTrue();
        assertThat(response.active()).isTrue();
    }

    @Test
    void shouldReturnActiveAcademicPeriod() {

        AcademicPeriod period = AcademicPeriod.builder()
                .id(UUID.randomUUID())
                .name("2026-1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .active(true)
                .build();

        when(repository.findByActiveTrue()).thenReturn(Optional.of(period));

        AcademicPeriod result = service.findActiveEntity();

        assertThat(result).isEqualTo(period);
    }

    @Test
    void shouldThrowExceptionWhenNoActiveAcademicPeriod() {

        when(repository.findByActiveTrue()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findActiveEntity())
                .isInstanceOf(BusinessException.class)
                .hasMessage("No active academic period");
    }

    @Test
    void shouldFindEntityById() {

        UUID id = UUID.randomUUID();

        AcademicPeriod period = AcademicPeriod.builder()
                .id(id)
                .name("2026-1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .active(false)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(period));

        AcademicPeriod result = service.findEntity(id);

        assertThat(result).isEqualTo(period);
    }

    @Test
    void shouldThrowExceptionWhenAcademicPeriodNotFound() {

        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findEntity(id))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Academic period not found");
    }
}
