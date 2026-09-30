package com.example.employeemanagement.modules.statistics;

public class DepartmentStat {

    private final Long departmentId;
    private final String departmentName;
    private final long employeeCount;

    public DepartmentStat(Long departmentId, String departmentName, Long employeeCount) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.employeeCount = employeeCount == null ? 0 : employeeCount;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public long getEmployeeCount() {
        return employeeCount;
    }
}
