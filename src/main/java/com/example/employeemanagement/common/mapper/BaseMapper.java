package com.example.employeemanagement.common.mapper;

/**
 * Converts between Entity and DTO. Each module implements its own mapper.
 *
 * @param <E>   entity
 * @param <REQ> request DTO (create / update)
 * @param <RES> DTO response
 */
public interface BaseMapper<E, REQ, RES> {

    E toEntity(REQ request);

    void updateEntity(REQ request, E entity);

    RES toResponse(E entity);
}
