package com.fortify.web.controller;

import com.fortify.web.dto.AutomationSettingDto;
import com.fortify.web.service.AutomationSettingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequestMapping("/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
public class AutomationSettingController {

    private final AutomationSettingService automationSettingService;

    public AutomationSettingController(AutomationSettingService automationSettingService) {
        this.automationSettingService = automationSettingService;
    }

    @GetMapping("/automation-schedule")
    public String automationScheduleSettingsForm(Model model) {
        AutomationSettingDto settings = automationSettingService.getAutomationSettings();
        model.addAttribute("settings", settings);
        model.addAttribute("webhookTypes", Arrays.asList("Slack", "Jira", "Teams", "Custom"));
        return "settings/automation-schedule-form";
    }

    @PostMapping("/automation-schedule")
    public String saveAutomationScheduleSettings(@ModelAttribute("settings") AutomationSettingDto settingsDto,
                                                 RedirectAttributes redirectAttributes) {
        automationSettingService.saveAutomationSettings(settingsDto);
        redirectAttributes.addFlashAttribute("message", "자동화 및 스케줄 환경설정이 성공적으로 저장되었습니다.");
        return "redirect:/admin/settings/automation-schedule";
    }
}
