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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("[my-log] 로그인 요청 - email: {}, password: {}", loginRequest.getEmail(), loginRequest.getPassword());
        LoginResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
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