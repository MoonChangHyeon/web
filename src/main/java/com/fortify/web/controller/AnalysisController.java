package com.fortify.web.controller;

import com.fortify.web.dto.AnalysisJobDto;
import com.fortify.web.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping
    public ResponseEntity<AnalysisJobDto.Response> createAnalysis(@RequestBody AnalysisJobDto.Request request) {
        AnalysisJobDto.Response response = analysisService.createAnalysisJob(request);
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<AnalysisJobDto.StatusResponse> getStatus(@PathVariable UUID jobId) {
        AnalysisJobDto.StatusResponse response = analysisService.getAnalysisJobStatus(jobId);
        return ResponseEntity.ok(response);
    }
}