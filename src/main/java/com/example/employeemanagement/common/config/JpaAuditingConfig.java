package com.example.employeemanagement.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables auditing for {@link com.example.employeemanagement.common.entity.BaseEntity}.
 * The current user is obtained from the "auditorAware" bean (see SecurityAuditorAware).
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {
}
