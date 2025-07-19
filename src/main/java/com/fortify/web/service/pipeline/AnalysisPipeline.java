package com.fortify.web.service.pipeline;

import com.fortify.web.domain.AnalysisJob;
import com.fortify.web.domain.AnalysisJobStatus;
import com.fortify.web.domain.FortifySetting;
import com.fortify.web.repository.AnalysisJobRepository;
import com.fortify.web.service.FortifySettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisPipeline {

    private final AnalysisJobRepository analysisJobRepository;
    private final WorkspaceManager workspaceManager;
    private final GitClient gitClient;
    private final FortifyExecutor fortifyExecutor;
    private final ReportExecutor reportExecutor;
    private final FortifySettingService fortifySettingService;

    @Async("taskExecutor")
    public void run(AnalysisJob job) {
        log.info("Starting analysis pipeline for job: {}", job.getJobId());
        Path workspacePath = null;
        String jobId = job.getJobId().toString();

        try {
            job.setStatus(AnalysisJobStatus.RUNNING);
            job.setStartedAt(LocalDateTime.now());
            analysisJobRepository.save(job);

            // 1. Workspace 준비
            workspacePath = workspaceManager.createWorkspace(jobId);
            log.info("Workspace created at: {}", workspacePath);

            // 2. Git 클론
            gitClient.cloneRepository(job.getRepoUrl(), job.getBranch(), workspacePath);

            // 3. Fortify Clean
            fortifyExecutor.clean(jobId, workspacePath);

            // 4. Fortify Translate
            fortifyExecutor.translate(jobId, workspacePath);

            // 5. Fortify Scan
            fortifyExecutor.scan(jobId, workspacePath);

            // 6. Report Generation
            reportExecutor.generatePdf(jobId, workspacePath);
            reportExecutor.generateXml(jobId, workspacePath);

            FortifySetting setting = fortifySettingService.getFortifySetting();
            if (setting != null) {
                job.setReportPdfPath(Path.of(setting.getPdfOutputDirectory()).resolve(jobId + ".pdf").toString());
                job.setReportXmlPath(Path.of(setting.getXmlOutputDirectory()).resolve(jobId + ".xml").toString());
            } else {
                log.warn("Fortify settings not found. Report paths might be incorrect.");
            }

            job.setStatus(AnalysisJobStatus.COMPLETED);
            job.setFinishedAt(LocalDateTime.now());
            analysisJobRepository.save(job);
            log.info("Analysis pipeline completed for job: {}", job.getJobId());

        } catch (Exception e) {
            log.error("Analysis pipeline failed for job: {}", job.getJobId(), e);
            job.setStatus(AnalysisJobStatus.FAILED);
            job.setFinishedAt(LocalDateTime.now());
            job.setErrorMessage(e.getMessage());
            analysisJobRepository.save(job);
        } finally {
            if (workspacePath != null) {
                try {
                    // workspaceManager.cleanupWorkspace(job.getJobId().toString());
                    // log.info("Workspace cleaned up for job: {}", job.getJobId());
                } catch (Exception e) {
                    log.error("Failed to cleanup workspace for job: {}", job.getJobId(), e);
                }
            }
        }
    }
}
