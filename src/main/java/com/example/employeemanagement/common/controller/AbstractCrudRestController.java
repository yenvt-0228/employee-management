package com.example.employeemanagement.common.controller;

import com.example.employeemanagement.common.dto.ApiResponse;
import com.example.employeemanagement.common.dto.PageResponse;
import com.example.employeemanagement.common.service.CrudService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * Standard REST CRUD. A subclass only needs @RestController + @RequestMapping and to call super(service).
 *
 * GET    /            paginated list (?page=0&size=20&sort=name,asc)
 * GET    /{id}        detail
 * POST   /            create (201)
 * PUT    /{id}        update
 * DELETE /{id}        delete
 */
public abstract class AbstractCrudRestController<REQ, RES> {

    protected final CrudService<REQ, RES, Long> service;

    protected AbstractCrudRestController(CrudService<REQ, RES, Long> service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RES>>> findAll(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(service.findAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RES>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RES>> create(@Valid @RequestBody REQ request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.create(request), "Tạo mới thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RES>> update(@PathVariable Long id, @Valid @RequestBody REQ request) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request), "Cập nhật thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.message("Xoá thành công"));
    }
}
