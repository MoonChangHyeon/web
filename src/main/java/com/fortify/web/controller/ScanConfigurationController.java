package com.fortify.web.controller;

import com.fortify.web.domain.ScanConfiguration;
import com.fortify.web.service.ScanConfigurationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/scan-configurations")
public class ScanConfigurationController {

    private final ScanConfigurationService scanConfigurationService;

    public ScanConfigurationController(ScanConfigurationService scanConfigurationService) {
        this.scanConfigurationService = scanConfigurationService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("configurations", scanConfigurationService.findAll());
        return "scan-configurations/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("configuration", new ScanConfiguration());
        return "scan-configurations/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("configuration") ScanConfiguration configuration, BindingResult result) {
        if (result.hasErrors()) {
            return "scan-configurations/form";
        }
        scanConfigurationService.save(configuration);
        return "redirect:/scan-configurations";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("configuration", scanConfigurationService.findById(id));
        return "scan-configurations/form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("configuration") ScanConfiguration configuration, BindingResult result) {
        if (result.hasErrors()) {
            return "scan-configurations/form";
        }
        configuration.setId(id);
        scanConfigurationService.save(configuration);
        return "redirect:/scan-configurations";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        scanConfigurationService.deleteById(id);
        return "redirect:/scan-configurations";
    }

    @PostMapping("/run/{id}")
    public String runScan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String result = scanConfigurationService.runScan(id);
        redirectAttributes.addFlashAttribute("scanResult", result);
        return "redirect:/scan-configurations";
    }
}
