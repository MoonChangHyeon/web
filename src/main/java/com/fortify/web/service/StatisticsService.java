package com.fortify.web.service;

import com.fortify.web.domain.AnalysisJob;
import com.fortify.web.repository.AnalysisJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private AnalysisJobRepository analysisJobRepository;

    public long getTotalJobs() {
        return analysisJobRepository.count();
    }

    public long getSuccessfulJobs() {
        return analysisJobRepository.countByStatus("SUCCESS");
    }

    public long getFailedJobs() {
        return analysisJobRepository.countByStatus("FAILURE");
    }

    public long getInProgressJobs() {
        return analysisJobRepository.countByStatus("IN_PROGRESS");
    }

    public Map<String, Long> getMonthlyAnalysisCounts() {
        List<AnalysisJob> jobs = analysisJobRepository.findAll();
        return jobs.stream()
                .filter(job -> job.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        job -> job.getCreatedAt().getMonth().toString(),
                        Collectors.counting()
                ));
    }
}
