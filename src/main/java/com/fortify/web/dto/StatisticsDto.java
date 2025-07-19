package com.fortify.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class StatisticsDto {
    private long totalProjects;
    private long totalAnalysisJobs;
    private Map<String, Long> analysisStatusDistribution;
    private String averageAnalysisDuration; // Format as String (e.g., "HH:mm:ss")
    private Map<String, Long> analysisJobTrends; // Date (String) -> Count
}
