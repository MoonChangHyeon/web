package com.fortify.web.controller;

import com.fortify.web.domain.ReportConfiguration;
import com.fortify.web.service.ReportConfigurationService;
import com.fortify.web.service.FortifySettingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/report-configurations")
public class ReportConfigurationController {

    private final ReportConfigurationService reportConfigurationService;
    private final FortifySettingService fortifySettingService;

    public ReportConfigurationController(ReportConfigurationService reportConfigurationService, FortifySettingService fortifySettingService) {
        this.reportConfigurationService = reportConfigurationService;
        this.fortifySettingService = fortifySettingService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("configurations", reportConfigurationService.findAll());
        return "report-configurations/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("configuration", new ReportConfiguration());
        model.addAttribute("templates", fortifySettingService.getReportTemplateFiles());
        return "report-configurations/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("configuration") ReportConfiguration configuration, BindingResult result) {
        if (result.hasErrors()) {
            return "report-configurations/form";
        }
        reportConfigurationService.save(configuration);
        return "redirect:/report-configurations";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("configuration", reportConfigurationService.findById(id));
        model.addAttribute("templates", fortifySettingService.getReportTemplateFiles());
        return "report-configurations/form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("configuration") ReportConfiguration configuration, BindingResult result) {
        if (result.hasErrors()) {
            return "report-configurations/form";
        }
        configuration.setId(id);
        reportConfigurationService.save(configuration);
        return "redirect:/report-configurations";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        reportConfigurationService.deleteById(id);
        return "redirect:/report-configurations";
    }

    @PostMapping("/generate/{id}")
    public String generateReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String result = reportConfigurationService.generateReport(id);
        redirectAttributes.addFlashAttribute("reportResult", result);
        return "redirect:/report-configurations";
    }
}
