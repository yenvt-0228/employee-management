package com.example.employeemanagement.modules.employee;

import com.example.employeemanagement.common.dto.PageResponse;
import com.example.employeemanagement.common.exception.ConflictException;
import com.example.employeemanagement.modules.department.DepartmentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

/**
 * Lab 6: Controller + Model + View (Thymeleaf), data binding and form handling.
 */
@Controller
@RequestMapping("/employees")
public class EmployeeViewController {

    private static final String FORM_VIEW = "employees/form";

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public EmployeeViewController(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    @GetMapping("/list")
    public String list(@PageableDefault(size = 10, sort = "id") Pageable pageable, Model model) {
        model.addAttribute("page", employeeService.findAll(pageable));
        model.addAttribute("pageUrl", "/employees/list");
        return "employees/list";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String name,
                         @RequestParam(required = false) Long departmentId,
                         @PageableDefault(size = 10, sort = "id") Pageable pageable,
                         Model model) {
        boolean searched = name != null || departmentId != null;
        if (searched) {
            PageResponse<EmployeeResponse> result = employeeService.search(name, departmentId, pageable);
            model.addAttribute("page", result);
            model.addAttribute("pageUrl", UriComponentsBuilder.fromPath("/employees/search")
                    .queryParamIfPresent("name", java.util.Optional.ofNullable(name))
                    .queryParamIfPresent("departmentId", java.util.Optional.ofNullable(departmentId))
                    .build().encode().toUriString());
        }
        model.addAttribute("name", name);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("departments", departmentService.findAll());
        return "employees/search";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("employee", new EmployeeRequest());
        return prepareForm(model, null);
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("employee") EmployeeRequest request, BindingResult bindingResult,
                      Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return prepareForm(model, null);
        }
        try {
            EmployeeResponse created = employeeService.create(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã thêm nhân viên " + created.getName() + " (" + created.getCode() + ")");
            return "redirect:/employees/list";
        } catch (ConflictException ex) {
            bindingResult.rejectValue(ex.getField(), "conflict", ex.getMessage());
            return prepareForm(model, null);
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getFormData(id));
        return prepareForm(model, id);
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("employee") EmployeeRequest request,
                       BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return prepareForm(model, id);
        }
        try {
            employeeService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật nhân viên #" + id);
            return "redirect:/employees/list";
        } catch (ConflictException ex) {
            bindingResult.rejectValue(ex.getField(), "conflict", ex.getMessage());
            return prepareForm(model, id);
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        employeeService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xoá nhân viên #" + id);
        return "redirect:/employees/list";
    }

    private String prepareForm(Model model, Long employeeId) {
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("departments", departmentService.findAll());
        return FORM_VIEW;
    }
}
