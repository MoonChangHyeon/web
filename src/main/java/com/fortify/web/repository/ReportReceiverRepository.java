package com.fortify.web.repository;

import com.fortify.web.domain.ReportReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportReceiverRepository extends JpaRepository<ReportReceiver, Long> {
}
