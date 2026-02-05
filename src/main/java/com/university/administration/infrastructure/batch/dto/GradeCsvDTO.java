package com.university.administration.infrastructure.batch.dto;

import com.university.administration.domain.enums.GradeType;
import lombok.Data;

import java.util.UUID;

@Data
public class GradeCsvDTO {

    private UUID studentId;
    private UUID courseOfferingId;
    private GradeType gradeType;
    private Double gradeValue;
}
