package com.example.employeemanagement.common;

import com.example.employeemanagement.common.util.UtilityService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UtilityServiceTest {

    private final UtilityService utilityService = new UtilityService();

    @Test
    void generateEmployeeCode_padsSequence() {
        assertThat(utilityService.generateEmployeeCode(7)).isEqualTo("EMP00007");
    }

    @Test
    void normalizeName_trimsAndCapitalizes() {
        assertThat(utilityService.normalizeName("  nguyễn   VĂN  an ")).isEqualTo("Nguyễn Văn An");
    }

    @Test
    void normalizeEmail_lowercases() {
        assertThat(utilityService.normalizeEmail(" An.Nguyen@Example.COM ")).isEqualTo("an.nguyen@example.com");
    }
}
