package com.example.employeemanagement.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Unified response format when an error occurs.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

    private final boolean success = false;
    private final int status;
    private final String code;
    private final String message;
    private final String path;
    private final Map<String, String> errors;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(int status, String code, String message, String path, Map<String, String> errors) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.path = path;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
