package com.bootcampbox.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import io.jsonwebtoken.JwtException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.bootcampbox.server.exception.AdminLoginException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException 발생: ", e);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", e.getMessage());
        errorResponse.put("error", "IllegalArgumentException");
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("ValidationException 발생: ", ex);
        
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        error.put("error", "ValidationError");
        error.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AdminLoginException.class)
    public ResponseEntity<Map<String, Object>> handleAdminLoginException(AdminLoginException e) {
        log.error("AdminLoginException 발생: ", e);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", e.getMessage());
        errorResponse.put("error", "AdminLoginException");
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<Map<String, Object>> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        log.error("InternalAuthenticationServiceException 발생: ", e);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "인증 서비스 오류가 발생했습니다.");
        errorResponse.put("error", "InternalAuthenticationServiceException");
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException e) {
        log.error("AuthenticationException 발생: ", e);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "이메일 또는 비밀번호가 올바르지 않습니다.");
        errorResponse.put("error", "AuthenticationException");
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("AccessDeniedException 발생: ", e);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "권한이 없습니다.");
        errorResponse.put("error", "AccessDeniedException");
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(JwtException e) {
        log.error("JwtException 발생: ", e);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "JWT 토큰이 유효하지 않습니다.");
        errorResponse.put("error", "JwtException");
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        log.error("Exception 발생: ", ex);
        
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "서버 오류가 발생했습니다.");
        error.put("error", ex.getClass().getSimpleName());
        error.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
} 