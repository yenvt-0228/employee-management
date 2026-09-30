package com.example.employeemanagement.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ"),
    MALFORMED_REQUEST(HttpStatus.BAD_REQUEST, "Request sai định dạng"),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "Tham số sai kiểu dữ liệu"),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "Thiếu tham số bắt buộc"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Phương thức HTTP không được hỗ trợ"),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content-Type không được hỗ trợ"),
    CONFLICT(HttpStatus.CONFLICT, "Dữ liệu bị trùng hoặc xung đột"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
