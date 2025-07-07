package com.fortify.web.service;

import com.fortify.web.dto.AnalysisJobDto;

import java.util.UUID;

public interface AnalysisService {
    AnalysisJobDto.Response createAnalysisJob(AnalysisJobDto.Request request);
    AnalysisJobDto.StatusResponse getAnalysisJobStatus(UUID jobId);
}
