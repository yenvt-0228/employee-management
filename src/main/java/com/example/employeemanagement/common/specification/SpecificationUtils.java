package com.example.employeemanagement.common.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

/**
 * Ready-made Specifications. A null/blank value returns null (Spring skips that condition),
 * so multiple optional conditions can be combined with Specification.where(...).and(...).
 */
public final class SpecificationUtils {

    private SpecificationUtils() {
    }

    /** field LIKE %value% (case-insensitive). Supports nested paths like "department.name". */
    public static <E> Specification<E> containsIgnoreCase(String fieldPath, String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String pattern = "%" + value.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(path(root, fieldPath)), pattern);
    }

    /** field = value. Supports nested paths like "department.id". */
    public static <E> Specification<E> equalTo(String fieldPath, Object value) {
        if (value == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(path(root, fieldPath), value);
    }

    @SuppressWarnings("unchecked")
    private static <T> Path<T> path(Root<?> root, String fieldPath) {
        Path<?> path = root;
        for (String part : fieldPath.split("\\.")) {
            path = path.get(part);
        }
        return (Path<T>) path;
    }
}
