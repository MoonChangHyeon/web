package com.fortify.web.controller;

import com.fortify.web.dto.AnalysisRequestDto;
import com.fortify.web.service.AnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/run")
    @PreAuthorize("hasRole('ADMIN')") // ADMIN 역할만 이 API를 호출할 수 있도록 제한
    public ResponseEntity<String> runAnalysis(@Valid @RequestBody AnalysisRequestDto requestDto) {
        try {
            String result = analysisService.runSourceAnalyzer(requestDto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("분석 실행 중 오류 발생: " + e.getMessage());
        }
    }
}