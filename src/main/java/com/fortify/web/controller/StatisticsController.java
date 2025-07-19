package com.fortify.web.controller;

import com.fortify.web.dto.StatisticsDto;
import com.fortify.web.service.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public String showStatisticsDashboard(Model model) {
        StatisticsDto statistics = statisticsService.getOverallStatistics();
        model.addAttribute("statistics", statistics);
        model.addAttribute("title", "분석 통계");
        return "statistics/dashboard";
    }
}