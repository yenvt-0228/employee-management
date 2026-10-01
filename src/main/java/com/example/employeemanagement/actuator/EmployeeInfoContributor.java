package com.example.employeemanagement.actuator;

import com.example.employeemanagement.modules.department.DepartmentRepository;
import com.example.employeemanagement.modules.employee.EmployeeRepository;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Adds figures to GET /actuator/info. */
@Component
public class EmployeeInfoContributor implements InfoContributor {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeInfoContributor(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("data", Map.of(
                "employees", employeeRepository.count(),
                "departments", departmentRepository.count()));
    }
}
