package com.fortify.web.service;

import com.fortify.web.domain.ScanConfiguration;
import java.util.List;

public interface ScanConfigurationService {
    List<ScanConfiguration> findAll();
    ScanConfiguration findById(Long id);
    ScanConfiguration save(ScanConfiguration scanConfiguration);
    void deleteById(Long id);
    String runScan(Long id);
}
