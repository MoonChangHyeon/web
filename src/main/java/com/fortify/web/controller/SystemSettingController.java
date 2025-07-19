package com.fortify.web.controller;

import com.fortify.web.dto.SystemSettingDto;
import com.fortify.web.service.SystemSettingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    public SystemSettingController(SystemSettingService systemSettingService) {
        this.systemSettingService = systemSettingService;
    }

    @GetMapping("/web-server")
    public String webServerSettingsForm(Model model) {
        SystemSettingDto settings = systemSettingService.getSystemSettings();
        model.addAttribute("settings", settings);
        return "settings/web-server-form";
    }

    @PostMapping("/web-server")
    public String saveWebServerSettings(@ModelAttribute("settings") SystemSettingDto settingsDto,
                                        RedirectAttributes redirectAttributes) {
        systemSettingService.saveSystemSettings(settingsDto);
        redirectAttributes.addFlashAttribute("message", "웹/서버 환경설정이 성공적으로 저장되었습니다.");
        return "redirect:/admin/settings/web-server";
    }
}
