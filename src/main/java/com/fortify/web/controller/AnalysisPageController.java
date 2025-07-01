package com.fortify.web.controller;

import com.fortify.web.dto.AnalysisRequestDto;
import com.fortify.web.service.AnalysisService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/analysis")
@PreAuthorize("hasRole('ADMIN')") // 이 컨트롤러의 모든 기능은 ADMIN만 사용 가능
public class AnalysisPageController {

    private final AnalysisService analysisService;

    public AnalysisPageController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/form")
    public String showAnalysisForm(Model model) {
        model.addAttribute("analysisRequest", new AnalysisRequestDto());
        return "analysis-form"; // templates/analysis-form.html 뷰를 반환
    }

    @PostMapping("/run")
    public String runAnalysisFromForm(
            @Valid @ModelAttribute("analysisRequest") AnalysisRequestDto request,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "analysis-form"; // 유효성 검사 실패 시, 폼으로 다시 돌아감
        }

        try {
            String result = analysisService.runSourceAnalyzer(request);
            model.addAttribute("analysisResult", result);
        } catch (Exception e) {
            model.addAttribute("analysisError", "분석 실행 중 오류 발생: " + e.getMessage());
        }

        return "analysis-result"; // templates/analysis-result.html 뷰를 반환
    }
}