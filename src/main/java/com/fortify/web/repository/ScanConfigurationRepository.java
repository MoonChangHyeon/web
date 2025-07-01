package com.fortify.web.repository;

import com.fortify.web.domain.ScanConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanConfigurationRepository extends JpaRepository<ScanConfiguration, Long> {
}
