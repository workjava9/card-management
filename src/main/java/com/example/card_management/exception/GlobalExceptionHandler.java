package com.example.card_management.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ProblemDetail pd(HttpStatus status, String title, String detail, HttpServletRequest req) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(status, detail);
        p.setTitle(title);
        p.setType(URI.create("https://httpstatuses.com/" + status.value()));
        p.setProperty("timestamp", OffsetDateTime.now());
        p.setProperty("path", req.getRequestURI());
        return p;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        ProblemDetail p = pd(HttpStatus.BAD_REQUEST, "Validation failed", "Request has invalid fields", req);
        p.setProperty("errors", errors);
        return p;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        return pd(HttpStatus.BAD_REQUEST, "Constraint violation", ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return pd(HttpStatus.BAD_REQUEST, "Type mismatch", ex.getMessage(), req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleDenied(AccessDeniedException ex, HttpServletRequest req) {
        return pd(HttpStatus.FORBIDDEN, "Access denied", ex.getMessage(), req);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleNotFound(UserNotFoundException ex, HttpServletRequest req) {
        return pd(HttpStatus.NOT_FOUND, "User not found", ex.getMessage(), req);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleBadCreds(InvalidCredentialsException ex, HttpServletRequest req) {
        return pd(HttpStatus.UNAUTHORIZED, "Invalid credentials", ex.getMessage(), req);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ProblemDetail handleFramework(ErrorResponseException ex, HttpServletRequest req) {
        ProblemDetail body = ex.getBody();
        body.setProperty("timestamp", OffsetDateTime.now());
        body.setProperty("path", req.getRequestURI());
        return body;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleOther(Exception ex, HttpServletRequest req) {
        return pd(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", ex.getMessage(), req);
    }
}
