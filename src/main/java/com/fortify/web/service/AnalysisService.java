package com.fortify.web.service;

import com.fortify.web.dto.AnalysisRequestDto;
import java.io.IOException;

public interface AnalysisService {
    String runSourceAnalyzer(AnalysisRequestDto requestDto) throws IOException, InterruptedException;
}