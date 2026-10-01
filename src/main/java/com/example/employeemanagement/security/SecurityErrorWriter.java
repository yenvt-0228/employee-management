package com.example.employeemanagement.security;

import com.example.employeemanagement.common.dto.ErrorResponse;
import com.example.employeemanagement.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

final class SecurityErrorWriter {

    private SecurityErrorWriter() {
    }

    static void write(ObjectMapper objectMapper, HttpServletRequest request, HttpServletResponse response,
                      ErrorCode code) throws IOException {
        response.setStatus(code.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ErrorResponse body = new ErrorResponse(code.getStatus().value(), code.name(), code.getDefaultMessage(),
                request.getRequestURI(), null);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
