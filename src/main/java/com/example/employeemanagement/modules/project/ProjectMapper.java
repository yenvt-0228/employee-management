package com.example.employeemanagement.modules.project;

import com.example.employeemanagement.common.mapper.BaseMapper;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link Project} entity and its request/response DTOs.
 */
@Component
public class ProjectMapper implements BaseMapper<Project, ProjectRequest, ProjectResponse> {

    @Override
    public Project toEntity(ProjectRequest request) {
        Project project = new Project();
        updateEntity(request, project);
        return project;
    }

    @Override
    public void updateEntity(ProjectRequest request, Project entity) {
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
    }

    @Override
    public ProjectResponse toResponse(Project entity) {
        ProjectResponse response = new ProjectResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        return response;
    }
}
