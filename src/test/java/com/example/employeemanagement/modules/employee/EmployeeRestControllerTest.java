package com.example.employeemanagement.modules.employee;

import com.example.employeemanagement.modules.department.Department;
import com.example.employeemanagement.modules.department.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EmployeeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Long departmentId;

    @BeforeEach
    void setUp() {
        departmentId = departmentRepository.save(new Department("Phòng Test", null)).getId();
    }

    private String employeeJson(String name, String email) {
        return String.format("{\"name\":\"%s\",\"email\":\"%s\",\"departmentId\":%d}", name, email, departmentId);
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotCreate_returns403() throws Exception {
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("An", "an@example.com")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCreates_returns201WithGeneratedCode() throws Exception {
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("nguyễn văn an", "An@Example.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code", startsWith("EMP")))
                .andExpect(jsonPath("$.data.name").value("Nguyễn Văn An"))
                .andExpect(jsonPath("$.data.email").value("an@example.com"))
                .andExpect(jsonPath("$.data.departmentName").value("Phòng Test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void invalidBody_returns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").value("Email không đúng định dạng"))
                .andExpect(jsonPath("$.errors.departmentId").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"An\",\"departmentId\":\"abc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void duplicateEmail_returns409() throws Exception {
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson("An", "dup@example.com"))).andExpect(status().isCreated());

        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson("Bình", "DUP@example.com")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @WithMockUser
    void notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/employees/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    @WithMockUser
    void invalidPathVariable_returns400() throws Exception {
        mockMvc.perform(get("/api/employees/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TYPE_MISMATCH"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void search_filtersByNameAndDepartment() throws Exception {
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson("Trần Thị Bình", "binh@example.com"))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson("Lê Văn Cường", "cuong@example.com"))).andExpect(status().isCreated());

        mockMvc.perform(get("/api/employees/search").param("name", "bình").param("departmentId", departmentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].email").value("binh@example.com"));
    }
}
