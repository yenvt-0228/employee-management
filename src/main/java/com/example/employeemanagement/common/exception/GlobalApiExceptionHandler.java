package com.example.employeemanagement.common.exception;

import com.example.employeemanagement.common.dto.ErrorResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized exception handling for every @RestController, returning {@link ErrorResponse}.
 * Takes precedence over WebExceptionHandler (used for Thymeleaf pages).
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(annotations = RestController.class)
public class GlobalApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalApiExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException ex, HttpServletRequest request) {
        Map<String, String> errors = null;
        if (ex instanceof ConflictException && ((ConflictException) ex).getField() != null) {
            errors = Map.of(((ConflictException) ex).getField(), ex.getMessage());
        }
        return build(ex.getErrorCode(), ex.getMessage(), request, errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "Không hợp lệ" : fe.getDefaultMessage(),
                        (first, second) -> first,
                        LinkedHashMap::new));
        return build(ErrorCode.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR.getDefaultMessage(), request, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "Request body bị thiếu hoặc không phải JSON hợp lệ";
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            message = String.format("Giá trị '%s' của field '%s' phải có kiểu %s",
                    ife.getValue(), fieldPath(ife), ife.getTargetType().getSimpleName());
        } else if (cause instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) cause;
            if (!mie.getPath().isEmpty()) {
                message = String.format("Field '%s' sai định dạng", fieldPath(mie));
            }
        }
        return build(ErrorCode.MALFORMED_REQUEST, message, request, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String expected = ex.getRequiredType() == null ? "hợp lệ" : ex.getRequiredType().getSimpleName();
        String message = String.format("Tham số '%s' phải có kiểu %s (giá trị nhận được: '%s')",
                ex.getName(), expected, ex.getValue());
        return build(ErrorCode.TYPE_MISMATCH, message, request, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return build(ErrorCode.MISSING_PARAMETER, "Thiếu tham số '" + ex.getParameterName() + "'", request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return build(ErrorCode.METHOD_NOT_ALLOWED, ex.getMessage(), request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return build(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getDefaultMessage(), request, null);
    }

    /** E.g. ?sort=foo where "foo" isn't a real property (Spring Data pageable/sort binding). */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorResponse> handlePropertyReference(PropertyReferenceException ex, HttpServletRequest request) {
        return build(ErrorCode.VALIDATION_ERROR,
                "Thuộc tính '" + ex.getPropertyName() + "' không hợp lệ để sắp xếp/tìm kiếm", request, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return build(ErrorCode.UNSUPPORTED_MEDIA_TYPE, ErrorCode.UNSUPPORTED_MEDIA_TYPE.getDefaultMessage(), request, null);
    }

    /** E.g. two concurrent requests both pass the pre-check (existsByEmailIgnoreCase) and one hits the unique constraint. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation at {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(ErrorCode.CONFLICT, "Dữ liệu bị trùng hoặc vi phạm ràng buộc", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getDefaultMessage(), request, null);
    }

    private ResponseEntity<ErrorResponse> build(ErrorCode code, String message, HttpServletRequest request,
                                                Map<String, String> errors) {
        ErrorResponse body = new ErrorResponse(code.getStatus().value(), code.name(), message,
                request.getRequestURI(), errors);
        return ResponseEntity.status(code.getStatus()).body(body);
    }

    private static String fieldPath(MismatchedInputException ex) {
        return ex.getPath().stream()
                .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                .collect(Collectors.joining("."));
    }
}
