package com.university.administration.infrastructure.persistence.repository;

import com.university.administration.infrastructure.persistence.entity.AcademicPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, UUID> {

    Optional<AcademicPeriod> findByName(String name);

    Optional<AcademicPeriod> findByActiveTrue();
    @Modifying
    @Query("UPDATE AcademicPeriod p SET p.active = false WHERE p.active = true")
    void deactivateAll();
}

