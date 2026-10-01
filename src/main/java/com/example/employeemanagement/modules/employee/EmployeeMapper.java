package com.example.employeemanagement.modules.employee;

import com.example.employeemanagement.common.mapper.BaseMapper;
import org.springframework.stereotype.Component;

/**
 * Only maps simple fields; the department relation is assigned by the service in the prepareEntity hook.
 */
@Component
public class EmployeeMapper implements BaseMapper<Employee, EmployeeRequest, EmployeeResponse> {

    @Override
    public Employee toEntity(EmployeeRequest request) {
        Employee employee = new Employee();
        updateEntity(request, employee);
        return employee;
    }

    @Override
    public void updateEntity(EmployeeRequest request, Employee entity) {
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPosition(request.getPosition());
        entity.setHireDate(request.getHireDate());
    }

    @Override
    public EmployeeResponse toResponse(Employee entity) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        response.setEmail(entity.getEmail());
        response.setPosition(entity.getPosition());
        response.setHireDate(entity.getHireDate());
        if (entity.getDepartment() != null) {
            response.setDepartmentId(entity.getDepartment().getId());
            response.setDepartmentName(entity.getDepartment().getName());
        }
        return response;
    }

    /** Used to populate the edit form with existing data. */
    public EmployeeRequest toRequest(Employee entity) {
        EmployeeRequest request = new EmployeeRequest();
        request.setName(entity.getName());
        request.setEmail(entity.getEmail());
        request.setPosition(entity.getPosition());
        request.setHireDate(entity.getHireDate());
        request.setDepartmentId(entity.getDepartment().getId());
        return request;
    }
}
