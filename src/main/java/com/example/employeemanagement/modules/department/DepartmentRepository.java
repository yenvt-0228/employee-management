package com.example.employeemanagement.modules.department;

import com.example.employeemanagement.common.repository.BaseRepository;
import com.example.employeemanagement.modules.statistics.DepartmentStat;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentRepository extends BaseRepository<Department> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query("select new com.example.employeemanagement.modules.statistics.DepartmentStat(d.id, d.name, count(e)) "
            + "from Department d left join d.employees e "
            + "group by d.id, d.name order by count(e) desc, d.name")
    List<DepartmentStat> countEmployeesByDepartment();
}
