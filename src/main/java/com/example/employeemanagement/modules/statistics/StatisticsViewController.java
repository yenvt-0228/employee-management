package com.example.employeemanagement.modules.statistics;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticsViewController {

    private final StatisticsService statisticsService;

    public StatisticsViewController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        model.addAttribute("stats", statisticsService.getStatistics());
        return "stats/index";
    }
}
