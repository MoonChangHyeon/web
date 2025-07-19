package com.fortify.web.repository;

import com.fortify.web.domain.AnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnalysisJobRepository extends JpaRepository<AnalysisJob, UUID> {
    long countByStatus(String status);
}
