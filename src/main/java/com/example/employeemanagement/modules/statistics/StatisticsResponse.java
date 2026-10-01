package com.example.employeemanagement.modules.statistics;

import java.util.List;

public class StatisticsResponse {

    private final long totalEmployees;
    private final long totalDepartments;
    private final long hiredLast12Months;
    private final List<DepartmentStat> byDepartment;
    private final List<MonthlyHireStat> hiringTrend;

    public StatisticsResponse(long totalEmployees, long totalDepartments, long hiredLast12Months,
                              List<DepartmentStat> byDepartment, List<MonthlyHireStat> hiringTrend) {
        this.totalEmployees = totalEmployees;
        this.totalDepartments = totalDepartments;
        this.hiredLast12Months = hiredLast12Months;
        this.byDepartment = byDepartment;
        this.hiringTrend = hiringTrend;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public long getTotalDepartments() {
        return totalDepartments;
    }

    public long getHiredLast12Months() {
        return hiredLast12Months;
    }

    public List<DepartmentStat> getByDepartment() {
        return byDepartment;
    }

    public List<MonthlyHireStat> getHiringTrend() {
        return hiringTrend;
    }
}
