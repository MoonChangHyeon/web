package com.fortify.web.dto;

import com.fortify.web.domain.AnalysisJobStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

public class AnalysisJobDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        private String repoUrl;
        private String branch = "main";
        private String credentialsId;
    }

    @Getter
    @Builder
    public static class Response {
        private UUID jobId;
        private AnalysisJobStatus status;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class StatusResponse {
        private UUID jobId;
        private AnalysisJobStatus status;
        private LocalDateTime startedAt;
        private LocalDateTime finishedAt;
        private String reportPdfUrl;
        private String reportXmlUrl;
        private String errorMessage;
    }
}
