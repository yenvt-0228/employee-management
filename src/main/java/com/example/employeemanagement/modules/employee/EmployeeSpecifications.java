package com.example.employeemanagement.modules.employee;

import com.example.employeemanagement.common.specification.SpecificationUtils;
import org.springframework.data.jpa.domain.Specification;

public final class EmployeeSpecifications {

    private EmployeeSpecifications() {
    }

    /** Filters by name (contains) and/or department; a null parameter is skipped. */
    public static Specification<Employee> filter(String name, Long departmentId) {
        return Specification.<Employee>where(SpecificationUtils.containsIgnoreCase("name", name))
                .and(SpecificationUtils.equalTo("department.id", departmentId));
    }
}
