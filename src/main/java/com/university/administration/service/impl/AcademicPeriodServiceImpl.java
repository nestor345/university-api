package com.university.administration.service.impl;

import com.university.administration.domain.dto.AcademicPeriodCreateDTO;
import com.university.administration.domain.dto.AcademicPeriodResponseDTO;
import com.university.administration.exceptions.BusinessException;
import com.university.administration.infrastructure.persistence.entity.AcademicPeriod;
import com.university.administration.infrastructure.persistence.repository.AcademicPeriodRepository;
import com.university.administration.service.AcademicPeriodService;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AcademicPeriodServiceImpl implements AcademicPeriodService {

    private final AcademicPeriodRepository repository;

    @Override
    public AcademicPeriodResponseDTO create(AcademicPeriodCreateDTO dto) {

        if (dto.startDate().isAfter(dto.endDate())) {
            throw new BusinessException("Start date must be before end date");
        }

        AcademicPeriod period = AcademicPeriod.builder()
                .name(dto.name())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .active(false)
                .build();

        repository.save(period);

        return map(period);
    }

    @Override
    public AcademicPeriodResponseDTO activate(UUID id) {

        repository.deactivateAll();

        AcademicPeriod period = findEntity(id);
        period.setActive(true);

        return map(period);
    }

    @Override
    public AcademicPeriod findActiveEntity() {
        return repository.findByActiveTrue()
                .orElseThrow(() -> new BusinessException("No active academic period"));
    }

    @Override
    public AcademicPeriod findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("Academic period not found"));
    }

    private AcademicPeriodResponseDTO map(AcademicPeriod p) {
        return new AcademicPeriodResponseDTO(
                p.getId(),
                p.getName(),
                p.getStartDate(),
                p.getEndDate(),
                p.getActive()
        );
    }
}

