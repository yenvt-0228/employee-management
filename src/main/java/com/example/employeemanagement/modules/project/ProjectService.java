package com.example.employeemanagement.modules.project;

import com.example.employeemanagement.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

/**
 * CRUD service for projects. Reuses find/create/update/delete from
 * {@link AbstractCrudService}; no extra business rules yet.
 */
@Service
public class ProjectService extends AbstractCrudService<Project, ProjectRequest, ProjectResponse> {

    public ProjectService(ProjectRepository repository, ProjectMapper mapper) {
        super(repository, mapper);
    }

    /** Display name used in log messages and 404 errors (e.g. "dự án" not found with id=5). */
    @Override
    protected String getResourceName() {
        return "dự án";
    }
}
