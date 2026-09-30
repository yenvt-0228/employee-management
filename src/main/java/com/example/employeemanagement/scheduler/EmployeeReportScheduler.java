package com.example.employeemanagement.scheduler;

import com.example.employeemanagement.modules.statistics.DepartmentStat;
import com.example.employeemanagement.modules.statistics.StatisticsService;
import com.example.employeemanagement.modules.statistics.StatisticsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Lab 8: scheduled task that logs an employee-count report.
 * Schedule is configured via app.scheduler.report-cron ("-" to disable).
 */
@Component
public class EmployeeReportScheduler {

    private static final Logger log = LoggerFactory.getLogger(EmployeeReportScheduler.class);

    private final StatisticsService statisticsService;

    public EmployeeReportScheduler(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Scheduled(cron = "${app.scheduler.report-cron}")
    public void reportEmployeeCount() {
        StatisticsResponse stats = statisticsService.getStatistics();
        log.info("[Report] total employees={}, departments={}, hired in last 12 months={}",
                stats.getTotalEmployees(), stats.getTotalDepartments(), stats.getHiredLast12Months());
        for (DepartmentStat stat : stats.getByDepartment()) {
            log.debug("[Report]   {} -> {}", stat.getDepartmentName(), stat.getEmployeeCount());
        }
    }
}
