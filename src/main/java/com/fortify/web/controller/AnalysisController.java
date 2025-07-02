package com.fortify.web.controller;

import com.fortify.web.service.AnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/request-analysis")
    public String requestAnalysis(@RequestParam("buildId") String buildId,
                                  @RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes) {
        try {
            analysisService.requestAnalysis(buildId, file);
            redirectAttributes.addFlashAttribute("message", "분석 요청에 성공했습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "분석 요청에 실패했습니다: " + e.getMessage());
        }
        return "redirect:/";
    }
}
