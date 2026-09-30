package com.example.employeemanagement.common.service;

import com.example.employeemanagement.common.dto.PageResponse;
import com.example.employeemanagement.common.entity.BaseEntity;
import com.example.employeemanagement.common.exception.ResourceNotFoundException;
import com.example.employeemanagement.common.mapper.BaseMapper;
import com.example.employeemanagement.common.repository.BaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Shared CRUD logic. A submodule only needs to supply a repository + mapper and override hooks when needed
 * (duplicate checks, assigning relations, generating codes...).
 *
 * @param <E>   entity
 * @param <REQ> DTO request
 * @param <RES> DTO response
 */
@Transactional(readOnly = true)
public abstract class AbstractCrudService<E extends BaseEntity, REQ, RES> implements CrudService<REQ, RES, Long> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final BaseRepository<E> repository;
    protected final BaseMapper<E, REQ, RES> mapper;

    protected AbstractCrudService(BaseRepository<E> repository, BaseMapper<E, REQ, RES> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /** Display name of the resource, used in logs and error messages. */
    protected abstract String getResourceName();

    @Override
    public PageResponse<RES> findAll(Pageable pageable) {
        return PageResponse.of(repository.findAll(pageable).map(mapper::toResponse));
    }

    @Override
    public List<RES> findAll() {
        return repository.findAll(Sort.by("id")).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<RES> search(Specification<E> spec, Pageable pageable) {
        return PageResponse.of(repository.findAll(spec, pageable).map(mapper::toResponse));
    }

    @Override
    public RES findById(Long id) {
        return mapper.toResponse(getEntity(id));
    }

    /** Gets the entity or throws 404. Other services can call this to assign relations. */
    public E getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceName(), id));
    }

    @Override
    @Transactional
    public RES create(REQ request) {
        beforeCreate(request);
        E entity = mapper.toEntity(request);
        prepareEntity(entity, request);
        E saved = repository.save(entity);
        afterCreate(saved);
        log.info("Created {} id={}", getResourceName(), saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RES update(Long id, REQ request) {
        E entity = getEntity(id);
        beforeUpdate(entity, request);
        mapper.updateEntity(request, entity);
        prepareEntity(entity, request);
        E saved = repository.save(entity);
        log.info("Updated {} id={}", getResourceName(), id);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        E entity = getEntity(id);
        beforeDelete(entity);
        repository.delete(entity);
        log.info("Deleted {} id={}", getResourceName(), id);
    }

    // ----- Hooks: override when needed -----

    /** Business checks before create (e.g. duplicate email). */
    protected void beforeCreate(REQ request) {
    }

    /** Business checks before update. */
    protected void beforeUpdate(E entity, REQ request) {
    }

    /** Runs after mapping request → entity, both on create and update (e.g. normalizing data, assigning relations). */
    protected void prepareEntity(E entity, REQ request) {
    }

    /** Runs after the entity already has an id (e.g. generating a code from the id). */
    protected void afterCreate(E entity) {
    }

    /** Checks constraints before delete. */
    protected void beforeDelete(E entity) {
    }
}
