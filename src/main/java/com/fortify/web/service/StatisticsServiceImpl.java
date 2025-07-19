package com.fortify.web.service;

import com.fortify.web.dto.StatisticsDto;
import com.fortify.web.repository.ProjectRepository;
import com.fortify.web.repository.AnalysisJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final ProjectRepository projectRepository;
    private final AnalysisJobRepository analysisJobRepository;

    public StatisticsServiceImpl(ProjectRepository projectRepository, AnalysisJobRepository analysisJobRepository) {
        this.projectRepository = projectRepository;
        this.analysisJobRepository = analysisJobRepository;
    }

    @Override
    public StatisticsDto getOverallStatistics() {
        StatisticsDto stats = new StatisticsDto();

        // 1. Total Projects
        stats.setTotalProjects(projectRepository.count());

        // 2. Total Analysis Jobs
        stats.setTotalAnalysisJobs(analysisJobRepository.count());

        // 3. Analysis Status Distribution
        List<Object[]> statusDistributionResult = analysisJobRepository.countAnalysisJobsByStatus();
        Map<String, Long> statusDistribution = statusDistributionResult.stream()
                .collect(Collectors.toMap(
                        row -> ((com.fortify.web.domain.AnalysisJobStatus) row[0]).name(),
                        row -> (Long) row[1],
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        stats.setAnalysisStatusDistribution(statusDistribution);

        // 4. Average Analysis Duration (for COMPLETED jobs)
        Double avgSeconds = analysisJobRepository.findAverageDurationForCompletedJobs();
        if (avgSeconds != null) {
            Duration duration = Duration.ofSeconds(avgSeconds.longValue());
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();
            stats.setAverageAnalysisDuration(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        } else {
            stats.setAverageAnalysisDuration("N/A");
        }

        // 5. Analysis Job Trends (e.g., daily count for last 30 days)
        List<Object[]> trendsResult = analysisJobRepository.findDailyAnalysisJobTrendsLast30Days();
        Map<String, Long> jobTrends = trendsResult.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), // Convert Date object to String
                        row -> ((Number) row[1]).longValue(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        stats.setAnalysisJobTrends(jobTrends);

        return stats;
    }
}
