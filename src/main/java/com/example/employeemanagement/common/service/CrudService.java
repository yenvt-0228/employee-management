package com.example.employeemanagement.common.service;

import com.example.employeemanagement.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrudService<REQ, RES, ID> {

    PageResponse<RES> findAll(Pageable pageable);

    List<RES> findAll();

    RES findById(ID id);

    RES create(REQ request);

    RES update(ID id, REQ request);

    void delete(ID id);
}
