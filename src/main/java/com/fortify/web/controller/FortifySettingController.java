package com.fortify.web.controller;

import com.fortify.web.domain.FortifySetting;
import com.fortify.web.service.FortifySettingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/fortify-settings")
public class FortifySettingController {

    private final FortifySettingService fortifySettingService;

    public FortifySettingController(FortifySettingService fortifySettingService) {
        this.fortifySettingService = fortifySettingService;
    }

    @GetMapping
    public String showSettingsForm(Model model) {
        FortifySetting setting = fortifySettingService.getFortifySetting();
        if (setting == null) {
            setting = new FortifySetting(); // 설정이 없으면 빈 객체 생성
        }
        model.addAttribute("fortifySetting", setting);
        model.addAttribute("reportTemplates", fortifySettingService.getReportTemplateFiles());
        return "fortify-settings/form";
    }

    @PostMapping
    public String saveSettings(@Valid @ModelAttribute("fortifySetting") FortifySetting fortifySetting, BindingResult result) {
        if (result.hasErrors()) {
            return "fortify-settings/form";
        }
        fortifySettingService.saveFortifySetting(fortifySetting);
        return "redirect:/fortify-settings?success";
    }
}
