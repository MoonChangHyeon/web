package com.fortify.web.service;

import com.fortify.web.domain.AnalysisJob;
import com.fortify.web.dto.AnalysisJobDto;
import com.fortify.web.repository.AnalysisJobRepository;
import com.fortify.web.service.FortifySettingService;
import com.fortify.web.service.pipeline.AnalysisPipeline;
import com.fortify.web.service.pipeline.WorkspaceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final AnalysisJobRepository analysisJobRepository;
    private final AnalysisPipeline analysisPipeline;
    private final FortifySettingService fortifySettingService;
    private final WorkspaceManager workspaceManager;

    @Override
    public AnalysisJobDto.Response createAnalysisJob(AnalysisJobDto.Request request) {
        AnalysisJob job = new AnalysisJob();
        job.setRepoUrl(request.getRepoUrl());
        job.setBranch(request.getBranch());
        job.setProjectSubdirectory(request.getProjectSubdirectory());
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

        String baseUrl = "/reports/"; // Base URL for serving static files

        return AnalysisJobDto.StatusResponse.builder()
                .jobId(job.getJobId())
                .repoUrl(job.getRepoUrl())
                .branch(job.getBranch())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .startedAt(job.getStartedAt())
                .finishedAt(job.getFinishedAt())
                .reportPdfUrl(job.getReportPdfPath() != null ? baseUrl + job.getJobId() + ".pdf" : null)
                .reportXmlUrl(job.getReportXmlPath() != null ? baseUrl + job.getJobId() + ".xml" : null)
                .errorMessage(job.getErrorMessage())
                .build();
    }

    @Override
    public List<AnalysisJobDto.StatusResponse> getAllAnalysisJobs() {
        return analysisJobRepository.findAll().stream()
                .map(job -> {
                    String baseUrl = "/reports/";
                    return AnalysisJobDto.StatusResponse.builder()
                            .jobId(job.getJobId())
                            .repoUrl(job.getRepoUrl())
                            .branch(job.getBranch())
                            .status(job.getStatus())
                            .createdAt(job.getCreatedAt())
                            .startedAt(job.getStartedAt())
                            .finishedAt(job.getFinishedAt())
                            .reportPdfUrl(job.getReportPdfPath() != null ? baseUrl + job.getJobId() + ".pdf" : null)
                            .reportXmlUrl(job.getReportXmlPath() != null ? baseUrl + job.getJobId() + ".xml" : null)
                            .errorMessage(job.getErrorMessage())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAnalysisJob(UUID jobId) {
        AnalysisJob job = analysisJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Analysis Job not found with ID: " + jobId));

        // Delete associated files
        try {
            // Delete FPR file
            if (job.getReportPdfPath() != null) { // reportPdfPath는 FPR 경로와 동일한 FPR 디렉토리에 저장되므로 FPR 경로로 사용
                Path fprPath = Path.of(job.getReportPdfPath()).getParent().getParent().resolve(jobId + ".fpr");
                Files.deleteIfExists(fprPath);
                log.info("Deleted FPR file: {}", fprPath);
            }

            // Delete PDF report
            if (job.getReportPdfPath() != null) {
                Path pdfPath = Path.of(job.getReportPdfPath());
                Files.deleteIfExists(pdfPath);
                log.info("Deleted PDF report: {}", pdfPath);
            }

            // Delete XML report
            if (job.getReportXmlPath() != null) {
                Path xmlPath = Path.of(job.getReportXmlPath());
                Files.deleteIfExists(xmlPath);
                log.info("Deleted XML report: {}", xmlPath);
            }

            // Clean up workspace directory
            workspaceManager.cleanupWorkspace(jobId.toString());
            log.info("Cleaned up workspace for job: {}", jobId);

        } catch (IOException e) {
            log.error("Failed to delete files for analysis job {}: {}", jobId, e.getMessage());
            // Optionally re-throw or handle more gracefully
        }

        // Delete job from database
        analysisJobRepository.delete(job);
        log.info("Deleted analysis job from database: {}", jobId);
    }
}
