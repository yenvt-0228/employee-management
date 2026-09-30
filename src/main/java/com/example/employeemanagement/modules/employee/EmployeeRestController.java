package com.example.employeemanagement.modules.employee;

import com.example.employeemanagement.common.controller.AbstractCrudRestController;
import com.example.employeemanagement.common.dto.ApiResponse;
import com.example.employeemanagement.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * CRUD inherited from AbstractCrudRestController; only adds the search API.
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeRestController extends AbstractCrudRestController<EmployeeRequest, EmployeeResponse> {

    private final EmployeeService employeeService;

    public EmployeeRestController(EmployeeService employeeService) {
        super(employeeService);
        this.employeeService = employeeService;
    }

    /** GET /api/employees/search?name=an&departmentId=1&page=0&size=10 */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long departmentId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.search(name, departmentId, pageable)));
    }
}
