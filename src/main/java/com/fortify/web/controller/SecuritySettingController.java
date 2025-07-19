package com.fortify.web.controller;

import com.fortify.web.dto.SecuritySettingDto;
import com.fortify.web.service.SecuritySettingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
public class SecuritySettingController {

    private final SecuritySettingService securitySettingService;

    public SecuritySettingController(SecuritySettingService securitySettingService) {
        this.securitySettingService = securitySettingService;
    }

    @GetMapping("/security-access")
    public String securityAccessSettingsForm(Model model) {
        SecuritySettingDto settings = securitySettingService.getSecuritySettings();
        model.addAttribute("settings", settings);
        model.addAttribute("allRolePolicies", Arrays.asList("admin", "user", "external"));
        model.addAttribute("twoFactorAuthOptions", Arrays.asList("none", "otp", "saml", "radius"));
        model.addAttribute("apiTokenScopeOptions", Arrays.asList("분석요청", "리포트출력", "설정변경", "전체"));
        return "settings/security-access-form";
    }

    @PostMapping("/security-access")
    public String saveSecurityAccessSettings(@ModelAttribute("settings") SecuritySettingDto settingsDto,
                                             RedirectAttributes redirectAttributes) {
        securitySettingService.saveSecuritySettings(settingsDto);
        redirectAttributes.addFlashAttribute("message", "보안 및 접근제어 환경설정이 성공적으로 저장되었습니다.");
        return "redirect:/admin/settings/security-access";
    }
}
