package com.example.employeemanagement.modules.employee;

import com.example.employeemanagement.common.dto.PageResponse;
import com.example.employeemanagement.common.exception.ConflictException;
import com.example.employeemanagement.common.service.AbstractCrudService;
import com.example.employeemanagement.common.util.UtilityService;
import com.example.employeemanagement.modules.department.DepartmentService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService extends AbstractCrudService<Employee, EmployeeRequest, EmployeeResponse> {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DepartmentService departmentService;
    private final UtilityService utilityService;
    private final Counter createdCounter;
    private final Counter deletedCounter;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper,
                           DepartmentService departmentService, UtilityService utilityService,
                           MeterRegistry meterRegistry) {
        super(employeeRepository, employeeMapper);
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.departmentService = departmentService;
        this.utilityService = utilityService;
        this.createdCounter = Counter.builder("ems.employees.created")
                .description("Số nhân viên được tạo mới")
                .register(meterRegistry);
        this.deletedCounter = Counter.builder("ems.employees.deleted")
                .description("Số nhân viên bị xoá")
                .register(meterRegistry);
    }

    @Override
    protected String getResourceName() {
        return "nhân viên";
    }

    public PageResponse<EmployeeResponse> search(String name, Long departmentId, Pageable pageable) {
        return search(EmployeeSpecifications.filter(name, departmentId), pageable);
    }

    public EmployeeRequest getFormData(Long id) {
        return employeeMapper.toRequest(getEntity(id));
    }

    @Override
    protected void beforeCreate(EmployeeRequest request) {
        String email = utilityService.normalizeEmail(request.getEmail());
        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("email", "Email " + email + " đã được sử dụng");
        }
    }

    @Override
    protected void beforeUpdate(Employee entity, EmployeeRequest request) {
        String email = utilityService.normalizeEmail(request.getEmail());
        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(email, entity.getId())) {
            throw new ConflictException("email", "Email " + email + " đã được sử dụng");
        }
    }

    @Override
    protected void prepareEntity(Employee entity, EmployeeRequest request) {
        entity.setName(utilityService.normalizeName(request.getName()));
        entity.setEmail(utilityService.normalizeEmail(request.getEmail()));
        entity.setDepartment(departmentService.getEntity(request.getDepartmentId()));
    }

    @Override
    protected void afterCreate(Employee entity) {
        entity.setCode(utilityService.generateEmployeeCode(entity.getId()));
        createdCounter.increment();
        log.info("Assigned code {} to employee '{}' (department={})",
                entity.getCode(), entity.getName(), entity.getDepartment().getName());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        super.delete(id);
        deletedCounter.increment();
    }
}
