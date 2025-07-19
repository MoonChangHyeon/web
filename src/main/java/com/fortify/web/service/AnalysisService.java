package com.fortify.web.service;

import com.fortify.web.dto.AnalysisJobDto;

import java.util.UUID;

import java.util.List;

public interface AnalysisService {
    AnalysisJobDto.Response createAnalysisJob(AnalysisJobDto.Request request);
    AnalysisJobDto.StatusResponse getAnalysisJobStatus(UUID jobId);
    List<AnalysisJobDto.StatusResponse> getAllAnalysisJobs();
    void deleteAnalysisJob(UUID jobId);
}
