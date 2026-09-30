package com.example.employeemanagement.modules.statistics;

import com.example.employeemanagement.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsRestController {

    private final StatisticsService statisticsService;

    public StatisticsRestController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<StatisticsResponse>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.ok(statisticsService.getStatistics()));
    }
}
