package com.fortify.web.service;

import com.fortify.web.domain.ReportConfiguration;
import java.util.List;

public interface ReportConfigurationService {
    List<ReportConfiguration> findAll();
    ReportConfiguration findById(Long id);
    ReportConfiguration save(ReportConfiguration reportConfiguration);
    void deleteById(Long id);
    String generateReport(Long id);
}
