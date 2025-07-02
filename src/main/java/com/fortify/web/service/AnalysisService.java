package com.fortify.web.service;

import com.fortify.web.domain.AnalysisRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AnalysisService {
    void requestAnalysis(String buildId, MultipartFile file) throws Exception;
    List<AnalysisRequest> getAllAnalysisRequests();
}