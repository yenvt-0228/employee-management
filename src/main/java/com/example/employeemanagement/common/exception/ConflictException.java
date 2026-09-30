package com.example.employeemanagement.common.exception;

public class ConflictException extends BaseException {

    private final String field;

    public ConflictException(String message) {
        this(null, message);
    }

    /**
     * @param field the field causing the conflict (e.g. "email"), used to show the error right at the form input
     */
    public ConflictException(String field, String message) {
        super(ErrorCode.CONFLICT, message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
