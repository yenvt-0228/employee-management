package com.example.employeemanagement.modules.project;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Request data for creating/updating a project, validated with Bean Validation.
 */
public class ProjectRequest {

    @NotBlank(message = "Tên dự án không được để trống")
    @Size(max = 150, message = "Tên dự án tối đa 150 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
