package com.example.employeemanagement.common.exception;

/**
 * Root business exception. Create a new exception by extending this and choosing a suitable {@link ErrorCode}.
 */
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        this(errorCode, errorCode.getDefaultMessage());
    }

    public BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
