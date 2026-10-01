package com.example.employeemanagement.modules.statistics;

public class MonthlyHireStat {

    private final int year;
    private final int month;
    private final long count;

    public MonthlyHireStat(Integer year, Integer month, Long count) {
        this.year = year;
        this.month = month;
        this.count = count == null ? 0 : count;
    }

    /** Label like "2026-09", used for the chart's time axis. */
    public String getLabel() {
        return String.format("%d-%02d", year, month);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public long getCount() {
        return count;
    }
}
