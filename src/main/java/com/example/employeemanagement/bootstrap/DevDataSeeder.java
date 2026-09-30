package com.example.employeemanagement.bootstrap;

import com.example.employeemanagement.modules.department.DepartmentRequest;
import com.example.employeemanagement.modules.department.DepartmentResponse;
import com.example.employeemanagement.modules.department.DepartmentService;
import com.example.employeemanagement.modules.employee.EmployeeRequest;
import com.example.employeemanagement.modules.employee.EmployeeRepository;
import com.example.employeemanagement.modules.employee.EmployeeService;
import com.example.employeemanagement.modules.user.Role;
import com.example.employeemanagement.modules.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Sample data for the dev profile: departments, employees and 2 accounts (admin/user).
 */
@Component
@Profile("dev")
@Order(2)
public class DevDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private static final String[][] DEPARTMENTS = {
            {"Phòng Kỹ thuật", "Phát triển và vận hành sản phẩm"},
            {"Phòng Nhân sự", "Tuyển dụng và quản lý nhân sự"},
            {"Phòng Kinh doanh", "Bán hàng và chăm sóc khách hàng"},
            {"Phòng Kế toán", "Tài chính và kế toán"},
            {"Phòng Marketing", "Truyền thông và thương hiệu"},
    };

    private static final String[][] EMPLOYEES = {
            {"Nguyễn Văn An", "an.nguyen", "Backend Developer"},
            {"Trần Thị Bình", "binh.tran", "HR Executive"},
            {"Lê Hoàng Cường", "cuong.le", "Sales Manager"},
            {"Phạm Thu Dung", "dung.pham", "Accountant"},
            {"Hoàng Minh Đức", "duc.hoang", "Frontend Developer"},
            {"Vũ Thị Giang", "giang.vu", "Content Marketer"},
            {"Đặng Quốc Huy", "huy.dang", "DevOps Engineer"},
            {"Bùi Ngọc Lan", "lan.bui", "Recruiter"},
            {"Đỗ Thanh Long", "long.do", "Sales Executive"},
            {"Ngô Thị Mai", "mai.ngo", "Chief Accountant"},
            {"Dương Văn Nam", "nam.duong", "QA Engineer"},
            {"Lý Thị Oanh", "oanh.ly", "SEO Specialist"},
            {"Phan Đình Phúc", "phuc.phan", "Tech Lead"},
            {"Trịnh Thu Quỳnh", "quynh.trinh", "Account Manager"},
            {"Mai Văn Sơn", "son.mai", "Mobile Developer"},
            {"Hồ Thị Trang", "trang.ho", "Designer"},
            {"Chu Minh Tuấn", "tuan.chu", "Data Engineer"},
            {"Tạ Thị Uyên", "uyen.ta", "Sales Executive"},
            {"Lương Văn Vinh", "vinh.luong", "Backend Developer"},
            {"Kiều Thị Xuân", "xuan.kieu", "Payroll Specialist"},
    };

    // Department index for each employee in the EMPLOYEES array
    private static final int[] EMPLOYEE_DEPARTMENT = {0, 1, 2, 3, 0, 4, 0, 1, 2, 3, 0, 4, 0, 2, 0, 4, 0, 2, 0, 3};

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final UserService userService;

    public DevDataSeeder(DepartmentService departmentService, EmployeeService employeeService,
                         EmployeeRepository employeeRepository, UserService userService) {
        this.departmentService = departmentService;
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean seededAdmin = seedIfMissing("admin", "admin123", "Quản trị viên", Role.ADMIN);
        boolean seededUser = seedIfMissing("user", "user123", "Người dùng", Role.USER);
        if (seededAdmin || seededUser) {
            log.warn("Profile 'dev' active: seeded well-known account(s) (admin/admin123, user/user123). "
                    + "Never run this profile against a real/shared database.");
        }
        if (employeeRepository.count() > 0) {
            return;
        }

        List<Long> departmentIds = new ArrayList<>();
        for (String[] d : DEPARTMENTS) {
            DepartmentRequest request = new DepartmentRequest();
            request.setName(d[0]);
            request.setDescription(d[1]);
            DepartmentResponse created = departmentService.create(request);
            departmentIds.add(created.getId());
        }

        LocalDate today = LocalDate.now();
        for (int i = 0; i < EMPLOYEES.length; i++) {
            EmployeeRequest request = new EmployeeRequest();
            request.setName(EMPLOYEES[i][0]);
            request.setEmail(EMPLOYEES[i][1] + "@example.com");
            request.setPosition(EMPLOYEES[i][2]);
            // Spread hire dates over the last ~18 months so the trend chart has data
            request.setHireDate(today.minusMonths((i * 7L) % 18).minusDays(i % 20));
            request.setDepartmentId(departmentIds.get(EMPLOYEE_DEPARTMENT[i]));
            employeeService.create(request);
        }
        log.info("Seeded {} departments and {} employees", DEPARTMENTS.length, EMPLOYEES.length);
    }

    /** Creates the account if missing; returns true iff a new account was actually created. */
    private boolean seedIfMissing(String username, String rawPassword, String fullName, Role role) {
        if (userService.exists(username)) {
            return false;
        }
        userService.createUser(username, rawPassword, fullName, role);
        return true;
    }
}
