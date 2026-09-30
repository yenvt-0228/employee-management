package com.example.employeemanagement.modules.department;

import com.example.employeemanagement.common.exception.ConflictException;
import com.example.employeemanagement.common.service.AbstractCrudService;
import com.example.employeemanagement.modules.employee.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService extends AbstractCrudService<Department, DepartmentRequest, DepartmentResponse> {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper mapper,
                             EmployeeRepository employeeRepository) {
        super(departmentRepository, mapper);
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    protected String getResourceName() {
        return "phòng ban";
    }

    @Override
    protected void beforeCreate(DepartmentRequest request) {
        if (departmentRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ConflictException("name", "Phòng ban '" + request.getName() + "' đã tồn tại");
        }
    }

    @Override
    protected void beforeUpdate(Department entity, DepartmentRequest request) {
        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), entity.getId())) {
            throw new ConflictException("name", "Phòng ban '" + request.getName() + "' đã tồn tại");
        }
    }

    @Override
    protected void beforeDelete(Department entity) {
        long count = employeeRepository.countByDepartmentId(entity.getId());
        if (count > 0) {
            throw new ConflictException("Không thể xoá phòng ban đang có " + count + " nhân viên");
        }
    }
}
