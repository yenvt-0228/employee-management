package com.example.employeemanagement.modules.department;

import com.example.employeemanagement.common.mapper.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper implements BaseMapper<Department, DepartmentRequest, DepartmentResponse> {

    @Override
    public Department toEntity(DepartmentRequest request) {
        Department department = new Department();
        updateEntity(request, department);
        return department;
    }

    @Override
    public void updateEntity(DepartmentRequest request, Department entity) {
        entity.setName(request.getName().trim());
        entity.setDescription(request.getDescription());
    }

    @Override
    public DepartmentResponse toResponse(Department entity) {
        return new DepartmentResponse(entity.getId(), entity.getName(), entity.getDescription());
    }
}
