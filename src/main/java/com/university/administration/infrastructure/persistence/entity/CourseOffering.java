package com.university.administration.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "course_offerings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOffering {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private AcademicPeriod period;

    @Column(nullable = false)
    private Integer maxStudents;

    @OneToMany(mappedBy = "courseOffering",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CourseOfferingSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "courseOffering")
    private List<Enrollment> enrollments;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}

