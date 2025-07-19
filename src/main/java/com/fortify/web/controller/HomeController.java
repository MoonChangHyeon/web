package com.fortify.web.controller;

import com.fortify.web.dto.AnalysisJobDto;
import com.fortify.web.service.AnalysisService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Controller
public class HomeController {

    private final AnalysisService analysisService;

    public HomeController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("analysisRequest", new AnalysisJobDto.Request()); // 분석 요청 폼을 위한 빈 객체
        model.addAttribute("analysisJobs", analysisService.getAllAnalysisJobs()); // 모든 분석 Job 목록
        return "home";
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/request-analysis")
    public String requestAnalysis(@Valid @ModelAttribute("analysisRequest") AnalysisJobDto.Request request,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "분석 요청에 실패했습니다. 입력값을 확인해주세요.");
            return "redirect:/";
        }
        try {
            analysisService.createAnalysisJob(request);
            redirectAttributes.addFlashAttribute("message", "분석 요청이 성공적으로 접수되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "분석 요청 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/";
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/analysis/delete/{jobId}")
    public String deleteAnalysisJob(@PathVariable UUID jobId, RedirectAttributes redirectAttributes) {
        try {
            analysisService.deleteAnalysisJob(jobId);
            redirectAttributes.addFlashAttribute("message", "분석 Job이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "분석 Job 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/fortify/scan/direct")
    public String runFortifyScanDirectly(Model model) {
        StringBuilder output = new StringBuilder();
        try {
            // gradlew 실행 경로를 'web' 디렉토리로 설정
            ProcessBuilder processBuilder = new ProcessBuilder("./gradlew", "fortifyScan", "-PbuildId=web_app_scan");
            processBuilder.directory(new java.io.File("web")); // Set working directory to 'web'
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            output.append("Exited with error code : ").append(exitCode).append("\n");

            if (exitCode == 0) {
                model.addAttribute("message", "Fortify SCA scan initiated successfully! Check console for details.");
            } else {
                model.addAttribute("message", "Fortify SCA scan failed! Error: " + output.toString());
            }

        } catch (IOException | InterruptedException e) {
            output.append("Error executing Fortify scan: ").append(e.getMessage()).append("\n");
            model.addAttribute("message", "Error executing Fortify SCA scan: " + e.getMessage());
            e.printStackTrace();
        }
        return "home"; // 스캔 결과 메시지를 home.html에 표시
    }
}
