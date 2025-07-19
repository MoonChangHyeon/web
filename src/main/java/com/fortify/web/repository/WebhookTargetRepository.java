package com.fortify.web.repository;

import com.fortify.web.domain.WebhookTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookTargetRepository extends JpaRepository<WebhookTarget, Long> {
}
