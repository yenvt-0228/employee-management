package com.example.employeemanagement.common.exception;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, String.format("Không tìm thấy %s với id = %s", resourceName, id));
    }
}
