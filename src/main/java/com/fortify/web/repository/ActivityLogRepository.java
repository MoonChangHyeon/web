package com.fortify.web.repository;

import com.fortify.web.domain.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Page<ActivityLog> findByActorContainingIgnoreCaseOrActionContainingIgnoreCaseOrEntityTypeContainingIgnoreCaseOrDetailsContainingIgnoreCase(
            String actor, String action, String entityType, String details, Pageable pageable);
}
