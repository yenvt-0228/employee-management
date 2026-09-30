package com.example.employeemanagement.modules.project;

import com.example.employeemanagement.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Project entity: id and audit columns (createdAt/updatedAt/createdBy/updatedBy)
 * are inherited from {@link BaseEntity}.
 */
@Entity
@Table(name = "project")
public class Project extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
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
