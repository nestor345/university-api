package com.university.administration.infrastructure.batch.processor;

import com.university.administration.infrastructure.batch.dto.GradeCsvDTO;
import com.university.administration.infrastructure.persistence.entity.Enrollment;
import com.university.administration.infrastructure.persistence.entity.Grade;
import com.university.administration.infrastructure.persistence.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GradeItemProcessor implements ItemProcessor<GradeCsvDTO, Grade> {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public Grade process(GradeCsvDTO item) {

        if (item.getGradeValue() < 0 || item.getGradeValue() > 5) {
            throw new IllegalArgumentException("Nota inválida: " + item.getGradeValue());
        }

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseOfferingId(
                        item.getStudentId(),
                        item.getCourseOfferingId()
                )
                .orElseThrow(() ->
                        new RuntimeException("Enrollment no encontrado para studentId=" + item.getStudentId())
                );

        return Grade.builder()
                .id(UUID.randomUUID())
                .enrollment(enrollment)
                .gradeType(item.getGradeType())
                .gradeValue(item.getGradeValue())
                .build();
    }
}
