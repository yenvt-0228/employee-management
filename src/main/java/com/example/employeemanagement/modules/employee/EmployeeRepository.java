package com.example.employeemanagement.modules.employee;

import com.example.employeemanagement.common.repository.BaseRepository;
import com.example.employeemanagement.modules.statistics.MonthlyHireStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends BaseRepository<Employee> {

    // Eagerly load department to avoid N+1 and LazyInitializationException when mapping to DTO.
    @Override
    @EntityGraph(attributePaths = "department")
    Page<Employee> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "department")
    Page<Employee> findAll(@Nullable Specification<Employee> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "department")
    Optional<Employee> findById(Long id);

    // Lab 4: query methods to search by name / department
    List<Employee> findByNameContainingIgnoreCase(String name);

    List<Employee> findByDepartmentId(Long departmentId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    long countByDepartmentId(Long departmentId);

    @Query("select new com.example.employeemanagement.modules.statistics.MonthlyHireStat("
            + "year(e.hireDate), month(e.hireDate), count(e)) "
            + "from Employee e where e.hireDate >= :from "
            + "group by year(e.hireDate), month(e.hireDate) "
            + "order by year(e.hireDate), month(e.hireDate)")
    List<MonthlyHireStat> countHiresByMonthSince(@Param("from") LocalDate from);
}
