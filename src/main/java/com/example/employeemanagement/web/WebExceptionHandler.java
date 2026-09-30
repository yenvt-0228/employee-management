package com.example.employeemanagement.web;

import com.example.employeemanagement.common.exception.BaseException;
import com.example.employeemanagement.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles exceptions for Thymeleaf pages: shows a friendly error page.
 * (REST API is already handled by GlobalApiExceptionHandler — this advice only matches
 * @Controller beans, so the same exception types are handled separately here for the web UI.)
 */
@ControllerAdvice(annotations = Controller.class)
public class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ModelAndView handleBase(BaseException ex, HttpServletResponse response) {
        return businessError(ex.getErrorCode(), ex.getMessage(), response);
    }

    /** E.g. /employees/list?sort=badfield: an invalid sort/query property. */
    @ExceptionHandler(PropertyReferenceException.class)
    public ModelAndView handlePropertyReference(PropertyReferenceException ex, HttpServletResponse response) {
        return businessError(ErrorCode.VALIDATION_ERROR,
                "Thuộc tính '" + ex.getPropertyName() + "' không hợp lệ để sắp xếp/tìm kiếm", response);
    }

    /** E.g. two concurrent form submits both pass the pre-check and one hits the unique constraint. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request,
                                                      HttpServletResponse response) {
        log.warn("Data integrity violation at {} {}: {}", request.getMethod(), request.getRequestURI(),
                ex.getMostSpecificCause().getClass().getSimpleName());
        return businessError(ErrorCode.CONFLICT, "Dữ liệu bị trùng hoặc vi phạm ràng buộc", response);
    }

    private ModelAndView businessError(ErrorCode code, String message, HttpServletResponse response) {
        response.setStatus(code.getStatus().value());
        ModelAndView mav = new ModelAndView("error/business");
        mav.addObject("status", code.getStatus().value());
        mav.addObject("title", code.getDefaultMessage());
        mav.addObject("message", message);
        return mav;
    }
}
