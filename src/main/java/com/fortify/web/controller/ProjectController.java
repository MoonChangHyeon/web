package com.fortify.web.controller;

import com.fortify.web.dto.AnalysisJobDto;
import com.fortify.web.dto.ProjectDto;
import com.fortify.web.service.ProjectService;
import com.fortify.web.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final AnalysisService analysisService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listProjects(Model model) {
        model.addAttribute("projects", projectService.getAllProjects());
        model.addAttribute("newProject", new ProjectDto());
        return "projects/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String createProject(@Valid @ModelAttribute("newProject") ProjectDto projectDto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newProject", result);
            redirectAttributes.addFlashAttribute("newProject", projectDto);
            redirectAttributes.addFlashAttribute("error", "프로젝트 생성에 실패했습니다. 입력값을 확인해주세요.");
            return "redirect:/projects";
        }
        try {
            projectService.createProject(projectDto);
            redirectAttributes.addFlashAttribute("message", "프로젝트가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "프로젝트 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/projects";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            projectService.deleteProject(id);
            redirectAttributes.addFlashAttribute("message", "프로젝트가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "프로젝트 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/projects";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("project", projectService.getProjectById(id));
        return "projects/edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/{id}")
    public String updateProject(@PathVariable Long id,
                                @Valid @ModelAttribute("project") ProjectDto projectDto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.project", result);
            redirectAttributes.addFlashAttribute("project", projectDto);
            redirectAttributes.addFlashAttribute("error", "프로젝트 수정에 실패했습니다. 입력값을 확인해주세요.");
            return "redirect:/projects/edit/" + id;
        }
        try {
            projectService.updateProject(id, projectDto);
            redirectAttributes.addFlashAttribute("message", "프로젝트가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "프로젝트 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/projects";
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/analyze/{projectId}")
    public String analyzeProject(@PathVariable Long projectId, RedirectAttributes redirectAttributes) {
        try {
            ProjectDto project = projectService.getProjectById(projectId);
            AnalysisJobDto.Request request = new AnalysisJobDto.Request();
            request.setRepoUrl(project.getRepoUrl());
            request.setBranch(project.getBranch());
            request.setCredentialsId(project.getCredentialsId());

            analysisService.createAnalysisJob(request);
            redirectAttributes.addFlashAttribute("message", "프로젝트 분석 요청이 성공적으로 접수되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "프로젝트 분석 요청 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/"; // 홈 화면으로 리다이렉트하여 분석 현황 확인
    }
}
