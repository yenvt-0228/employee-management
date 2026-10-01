package com.example.employeemanagement.modules.department;

import com.example.employeemanagement.common.controller.AbstractCrudRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departments")
public class DepartmentRestController extends AbstractCrudRestController<DepartmentRequest, DepartmentResponse> {

    public DepartmentRestController(DepartmentService service) {
        super(service);
    }
}
