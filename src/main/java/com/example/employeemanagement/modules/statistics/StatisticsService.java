package com.example.employeemanagement.modules.statistics;

import com.example.employeemanagement.modules.department.DepartmentRepository;
import com.example.employeemanagement.modules.employee.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    static final int TREND_MONTHS = 12;

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final Clock clock;

    public StatisticsService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.clock = Clock.systemDefaultZone();
    }

    public StatisticsResponse getStatistics() {
        List<MonthlyHireStat> trend = getHiringTrend();
        long hiredLast12Months = trend.stream().mapToLong(MonthlyHireStat::getCount).sum();
        return new StatisticsResponse(
                employeeRepository.count(),
                departmentRepository.count(),
                hiredLast12Months,
                departmentRepository.countEmployeesByDepartment(),
                trend);
    }

    /** Number of employees hired per month over the last 12 months; a month with none still returns 0. */
    public List<MonthlyHireStat> getHiringTrend() {
        YearMonth start = YearMonth.now(clock).minusMonths(TREND_MONTHS - 1L);
        Map<YearMonth, MonthlyHireStat> byMonth = employeeRepository.countHiresByMonthSince(start.atDay(1)).stream()
                .collect(Collectors.toMap(s -> YearMonth.of(s.getYear(), s.getMonth()), Function.identity()));

        List<MonthlyHireStat> result = new ArrayList<>(TREND_MONTHS);
        for (int i = 0; i < TREND_MONTHS; i++) {
            YearMonth ym = start.plusMonths(i);
            MonthlyHireStat stat = byMonth.get(ym);
            result.add(stat != null ? stat : new MonthlyHireStat(ym.getYear(), ym.getMonthValue(), 0L));
        }
        return result;
    }
}
