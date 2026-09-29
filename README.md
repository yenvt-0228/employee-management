# Employee Management System

Mini project học Spring Boot: quản lý nhân viên, đăng ký/đăng nhập, phân quyền Admin/User và thống kê.

**Công nghệ:** Spring Boot 2.7 (Java 11) · Spring Data JPA · Spring Security · Thymeleaf · H2 (dev) / MySQL (prod) · Actuator

## Chạy nhanh

```bash
./mvnw spring-boot:run          # profile dev, H2 in-memory, có sẵn dữ liệu mẫu
./mvnw test                     # chạy test
```

Mở http://localhost:8080 và đăng nhập bằng một trong hai tài khoản mẫu (chỉ có ở profile dev):

| Tài khoản | Mật khẩu | Quyền |
|---|---|---|
| `admin` | `admin123` | Thêm / sửa / xoá, actuator |
| `user` | `user123` | Xem, tìm kiếm, thống kê |

H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:employee_db`, user `sa`).

### Chạy với MySQL (profile prod)

```bash
docker compose up -d
SPRING_PROFILES_ACTIVE=prod DB_PASSWORD=ems_password \
APP_ADMIN_USERNAME=admin APP_ADMIN_PASSWORD='doi-mat-khau-nay' \
./mvnw spring-boot:run
```

Biến môi trường: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `APP_ADMIN_USERNAME`, `APP_ADMIN_PASSWORD`. Log được ghi vào `logs/`.

## Cấu trúc

```
com.example.employeemanagement
├── common/                  ← BASE TÁI SỬ DỤNG (không phụ thuộc module nào)
│   ├── entity/BaseEntity             id + createdAt/updatedAt/createdBy/updatedBy (JPA Auditing)
│   ├── repository/BaseRepository     JpaRepository + JpaSpecificationExecutor
│   ├── mapper/BaseMapper             Entity ⇄ DTO
│   ├── service/CrudService           interface CRUD
│   ├── service/AbstractCrudService   CRUD + phân trang + search(Specification) + hooks
│   ├── controller/AbstractCrudRestController   GET/POST/PUT/DELETE chuẩn
│   ├── dto/ApiResponse, PageResponse, ErrorResponse
│   ├── exception/ErrorCode, BaseException, ResourceNotFoundException, ConflictException,
│   │             GlobalApiExceptionHandler
│   ├── specification/SpecificationUtils   containsIgnoreCase, equalTo (hỗ trợ "a.b.c")
│   ├── util/UtilityService           (Lab 2)
│   └── config/AppConfig, JpaAuditingConfig
├── security/                SecurityConfig, UserDetailsService, JSON 401/403, AuditorAware
├── modules/
│   ├── employee/            Entity, Repository, DTO, Mapper, Service, REST + View controller
│   ├── department/
│   ├── user/                Đăng ký / đăng nhập
│   ├── statistics/          Thống kê theo phòng ban, xu hướng tuyển dụng
│   └── lab/                 Lab 1 (/hello), Lab 3 (API in-memory)
├── web/                     HomeController, WebExceptionHandler (trang lỗi Thymeleaf)
├── scheduler/               @Scheduled báo cáo định kỳ
├── actuator/                InfoContributor
└── bootstrap/               Dữ liệu mẫu (dev), tạo admin từ biến môi trường
```

## Thêm module mới bằng base

Ví dụ module `Project`: chỉ cần viết 6 file, và không phải viết lại CRUD, phân trang, validation hay xử lý lỗi.

```java
// 1. Entity: kế thừa BaseEntity để có sẵn id và các cột audit
@Entity
public class Project extends BaseEntity {
    private String name;
    // getter/setter
}

// 2. Repository
public interface ProjectRepository extends BaseRepository<Project> { }

// 3. DTO: ProjectRequest (có @NotBlank...), ProjectResponse

// 4. Mapper
@Component
public class ProjectMapper implements BaseMapper<Project, ProjectRequest, ProjectResponse> { ... }

// 5. Service: override hook khi cần
@Service
public class ProjectService extends AbstractCrudService<Project, ProjectRequest, ProjectResponse> {
    public ProjectService(ProjectRepository repo, ProjectMapper mapper) { super(repo, mapper); }
    @Override protected String getResourceName() { return "dự án"; }
    @Override protected void beforeCreate(ProjectRequest req) { /* kiểm tra trùng... */ }
}

// 6. Controller: có sẵn 5 endpoint CRUD
@RestController
@RequestMapping("/api/projects")
public class ProjectRestController extends AbstractCrudRestController<ProjectRequest, ProjectResponse> {
    public ProjectRestController(ProjectService service) { super(service); }
}
```

Hook trong `AbstractCrudService` (chạy theo thứ tự):

| Thao tác | Thứ tự |
|---|---|
| create | `beforeCreate` → `mapper.toEntity` → `prepareEntity` → save → `afterCreate` |
| update | `getEntity` (404) → `beforeUpdate` → `mapper.updateEntity` → `prepareEntity` → save |
| delete | `getEntity` (404) → `beforeDelete` → delete |

Tìm kiếm động: `service.search(Specification.where(SpecificationUtils.containsIgnoreCase("name", q)).and(...), pageable)`.

Lỗi nghiệp vụ mới: tạo class kế thừa `BaseException` với một `ErrorCode` (hoặc thêm giá trị mới vào `ErrorCode`). `GlobalApiExceptionHandler` sẽ tự trả về JSON đúng HTTP status.

## Định dạng API

Khi thành công:
```json
{ "success": true, "message": "OK", "data": { ... }, "timestamp": "..." }
```
Khi có lỗi:
```json
{ "success": false, "status": 400, "code": "VALIDATION_ERROR", "message": "Dữ liệu không hợp lệ",
  "path": "/api/employees", "errors": { "email": "Email không đúng định dạng" }, "timestamp": "..." }
```

## Endpoint

REST API dùng HTTP Basic, ví dụ `curl -u admin:admin123 ...`. Quyền USER chỉ được gọi GET; POST/PUT/DELETE cần quyền ADMIN.

| Method | URL | Mô tả |
|---|---|---|
| GET | `/hello` | Lab 1 |
| GET/POST | `/api/lab3/employees[?keyword=]`, `/api/lab3/employees/{id}` | Lab 3: in-memory, không cần đăng nhập |
| GET | `/api/employees?page=0&size=20&sort=name,asc` | Danh sách có phân trang |
| GET | `/api/employees/search?name=&departmentId=` | Tìm kiếm |
| GET/POST/PUT/DELETE | `/api/employees[/{id}]` | CRUD |
| GET/POST/PUT/DELETE | `/api/departments[/{id}]` | CRUD phòng ban |
| GET | `/api/statistics` | Thống kê |
| GET | `/actuator/health`, `/actuator/info` | Công khai |
| GET | `/actuator/metrics/ems.employees.created` | Cần quyền ADMIN |

Giao diện web: `/employees/list`, `/employees/search`, `/employees/add`, `/employees/{id}/edit`, `/statistics`, `/login`, `/register`.

## Lab nằm ở đâu

| Lab | Nội dung | File chính |
|---|---|---|
| 1 | Hello World | `modules/lab/HelloController` |
| 2 | Bean & IoC | `common/util/UtilityService` (@Service), `common/config/AppConfig` (@Bean PasswordEncoder), constructor injection trong `EmployeeService`, `UserService` |
| 3 | REST cơ bản | `modules/lab/InMemoryEmployeeController` |
| 4 | JPA | `modules/employee/Employee`, `modules/department/Department` (@ManyToOne/@OneToMany), `EmployeeRepository`, `EmployeeSpecifications` |
| 5 | Validation & Exception | `EmployeeRequest`, `common/exception/GlobalApiExceptionHandler`, BindingResult trong `EmployeeViewController` |
| 6 | Thymeleaf | `EmployeeViewController`, `templates/employees/*` |
| 7 | Logging & Profiles | log trong `AbstractCrudService`/`EmployeeService`, `application-{dev,prod}.yml`, `logback-spring.xml` |
| 8 | Actuator & Scheduling | `actuator/EmployeeInfoContributor`, counter Micrometer trong `EmployeeService`, `scheduler/EmployeeReportScheduler` |

## Ghi chú

Máy hiện chỉ có JDK 11, nên project dùng Spring Boot 2.7 (`javax.*`). Khi lên Spring Boot 3 (cần JDK 17+), bạn làm các bước sau:
- đổi `javax.persistence` / `javax.validation` / `javax.servlet` sang `jakarta.*`;
- đổi `thymeleaf-extras-springsecurity5` sang `thymeleaf-extras-springsecurity6`;
- trong `SecurityConfig`, đổi `antMatcher(s)` sang `securityMatcher` / `requestMatchers`.
