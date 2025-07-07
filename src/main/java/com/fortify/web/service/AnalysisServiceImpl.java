package com.fortify.web.service;

import com.fortify.web.domain.AnalysisJob;
import com.fortify.web.dto.AnalysisJobDto;
import com.fortify.web.repository.AnalysisJobRepository;
import com.fortify.web.service.pipeline.AnalysisPipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final AnalysisJobRepository analysisJobRepository;
    private final AnalysisPipeline analysisPipeline;

    @Override
    public AnalysisJobDto.Response createAnalysisJob(AnalysisJobDto.Request request) {
        AnalysisJob job = new AnalysisJob();
        job.setRepoUrl(request.getRepoUrl());
        job.setBranch(request.getBranch());
        // credentialsId는 나중에 처리

        job = analysisJobRepository.save(job);

        analysisPipeline.run(job);

        return AnalysisJobDto.Response.builder()
                .jobId(job.getJobId())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .build();
    }

    @Override
    public AnalysisJobDto.StatusResponse getAnalysisJobStatus(UUID jobId) {
        AnalysisJob job = analysisJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid job ID: " + jobId));

        String baseUrl = "/reports/" + jobId + "/"; // Assuming a /reports/{jobId}/ endpoint for serving static files

        return AnalysisJobDto.StatusResponse.builder()
                .jobId(job.getJobId())
                .status(job.getStatus())
                .startedAt(job.getStartedAt())
                .finishedAt(job.getFinishedAt())
                .reportPdfUrl(job.getReportPdfPath() != null ? baseUrl + jobId + ".pdf" : null)
                .reportXmlUrl(job.getReportXmlPath() != null ? baseUrl + jobId + ".xml" : null)
                .errorMessage(job.getErrorMessage())
                .build();
    }
}
