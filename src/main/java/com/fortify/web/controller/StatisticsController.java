package com.fortify.web.controller;

import com.fortify.web.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/statistics")
    public String getStatistics(Model model) {
        model.addAttribute("totalJobs", statisticsService.getTotalJobs());
        model.addAttribute("successfulJobs", statisticsService.getSuccessfulJobs());
        model.addAttribute("failedJobs", statisticsService.getFailedJobs());
        model.addAttribute("inProgressJobs", statisticsService.getInProgressJobs());
        model.addAttribute("monthlyAnalysisCounts", statisticsService.getMonthlyAnalysisCounts());
        return "statistics/dashboard";
    }
}
