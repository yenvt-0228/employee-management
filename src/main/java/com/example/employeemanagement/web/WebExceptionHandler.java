package com.example.employeemanagement.web;

import com.example.employeemanagement.common.exception.BaseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * Handles exceptions for Thymeleaf pages: shows a friendly error page.
 * (REST API is already handled by GlobalApiExceptionHandler.)
 */
@ControllerAdvice(annotations = Controller.class)
public class WebExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ModelAndView handleBase(BaseException ex, HttpServletResponse response) {
        response.setStatus(ex.getErrorCode().getStatus().value());
        ModelAndView mav = new ModelAndView("error/business");
        mav.addObject("status", ex.getErrorCode().getStatus().value());
        mav.addObject("title", ex.getErrorCode().getDefaultMessage());
        mav.addObject("message", ex.getMessage());
        return mav;
    }
}
