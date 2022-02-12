package com.rigel.ExpenseTracker.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public @Override void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        log.error("Error logging in: {}", e.getMessage());
        response.setHeader("error", e.getMessage());
        response.setStatus(UNAUTHORIZED.value());
        response.setContentType(APPLICATION_JSON_VALUE);

        Map<String, Object> error = new HashMap<>();
        error.put("time", new Date().getTime());
        error.put("error_type", UNAUTHORIZED);
        error.put("status_code", UNAUTHORIZED.value());
        error.put("message", e.getMessage());
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}