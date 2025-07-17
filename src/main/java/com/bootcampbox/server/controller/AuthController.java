package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.LoginRequestDto;
import com.bootcampbox.server.dto.LoginResponseDto;
import com.bootcampbox.server.dto.DuplicateCheckDto;
import com.bootcampbox.server.service.AuthService;
import com.bootcampbox.server.service.EmailService;
import com.bootcampbox.server.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import com.bootcampbox.server.exception.AdminLoginException;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("[my-log] 로그인 요청 - email: {}, password: {}", loginRequest.getEmail(), loginRequest.getPassword());
        try {
            LoginResponseDto response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (AdminLoginException e) {
            log.error("관리자 로그인 실패: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "AdminLoginException");
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (BadCredentialsException e) {
            log.error("로그인 실패 (BadCredentials): ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "BadCredentialsException");
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            log.error("로그인 실패 (기타): ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "로그인 중 오류가 발생했습니다.");
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok("로그아웃되었습니다.");
    }

    @PostMapping("/send-email-code")
    public ResponseEntity<Map<String, Object>> sendEmailCode(@RequestBody DuplicateCheckDto.EmailVerificationRequest request) {
        String code = emailService.generateVerificationCode();
        emailService.sendVerificationCode(request.getEmail(), code);
        emailVerificationService.saveVerificationCode(request.getEmail(), code);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "인증코드가 이메일로 발송되었습니다. (모의발송)");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("verificationCode", code); // 개발/테스트용 인증코드 포함
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email-code")
    public ResponseEntity<Map<String, Object>> verifyEmailCode(@RequestBody DuplicateCheckDto.EmailCodeVerifyRequest request) {
        boolean result = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("timestamp", LocalDateTime.now().toString());
        
        if (result) {
            response.put("message", "이메일 인증이 완료되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "인증코드가 올바르지 않거나 만료되었습니다.");
            response.put("error", "INVALID_CODE");
            return ResponseEntity.badRequest().body(response);
        }
    }
} 