package com.university.administration.persistence.entity;

import com.university.administration.domain.enums.GradeType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "grades",
        uniqueConstraints = @UniqueConstraint(columnNames = {"enrollment_id", "grade_type"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeType gradeType;

    @Column(nullable = false)
    private Double gradeValue;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}

