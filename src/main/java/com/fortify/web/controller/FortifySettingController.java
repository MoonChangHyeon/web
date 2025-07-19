package com.fortify.web.controller;

import com.fortify.web.domain.FortifySetting;
import com.fortify.web.service.FortifySettingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Files;
import java.nio.file.Paths;

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

        // fpr 및 xml 출력 디렉토리 존재 여부 확인 및 모델에 추가
        if (setting.getFprOutputDirectory() != null) {
            model.addAttribute("fprOutputDirectoryExists", Files.exists(Paths.get(setting.getFprOutputDirectory())));
        }
        if (setting.getXmlOutputDirectory() != null) {
            model.addAttribute("xmlOutputDirectoryExists", Files.exists(Paths.get(setting.getXmlOutputDirectory())));
        }

        return "fortify-settings/form";
    }

    @PostMapping
    public String saveSettings(@ModelAttribute("fortifySetting") FortifySetting fortifySetting) {
        fortifySettingService.saveFortifySetting(fortifySetting);
        return "redirect:/fortify-settings?success";
    }
}
