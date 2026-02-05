package com.university.administration.infrastructure.persistence.repository;

import com.university.administration.infrastructure.persistence.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}

