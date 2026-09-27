package com.ecommerce.sb_ecom.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Makes the security filter chain answer in the same JSON shape as the rest of the API
 * (ErrorResponse), and with the right status: 401 when the caller is not signed in or
 * their token is bad/expired, 403 when they are signed in but not allowed.
 * Without this, Spring Security replies 403 with an empty body for both cases, so a
 * client can't tell "session expired" from "not allowed".
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        write(response, HttpServletResponse.SC_UNAUTHORIZED, "Sign in to continue.");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        write(response, HttpServletResponse.SC_FORBIDDEN, "You don't have access to that.");
    }

    // Messages here are fixed strings with no user input, so building the JSON by hand is safe.
    private void write(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"success\":false,\"status\":" + status
                + ",\"message\":\"" + message.replace("\"", "\\\"")
                + "\",\"timestamp\":\"" + LocalDateTime.now() + "\"}");
    }
}
