package com.example.employeemanagement.modules.project;

import com.example.employeemanagement.common.controller.AbstractCrudRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CRUD REST API for projects, inherited entirely from {@link AbstractCrudRestController}.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectRestController extends AbstractCrudRestController<ProjectRequest, ProjectResponse> {

    public ProjectRestController(ProjectService service) {
        super(service);
    }
}
