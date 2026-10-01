package com.example.employeemanagement.common.util;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Lab 2: a self-defined bean using @Service, injected into other services/controllers.
 */
@Service
public class UtilityService {

    private static final String EMPLOYEE_CODE_PREFIX = "EMP";

    /** 7 → "EMP00007" */
    public String generateEmployeeCode(long sequence) {
        return String.format("%s%05d", EMPLOYEE_CODE_PREFIX, sequence);
    }

    /** "  nguyễn   văn  an " → "Nguyễn Văn An" */
    public String normalizeName(String raw) {
        if (!StringUtils.hasText(raw)) {
            return raw;
        }
        return Arrays.stream(raw.trim().split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" "));
    }

    public String normalizeEmail(String raw) {
        return raw == null ? null : raw.trim().toLowerCase(Locale.ROOT);
    }
}
