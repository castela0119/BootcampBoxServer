package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.PasswordResetDto;
import com.bootcampbox.server.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetDto.ResetPasswordResponse> resetPassword(
            @Valid @RequestBody PasswordResetDto.ResetPasswordRequest request) {
        try {
            log.info("비밀번호 재설정 요청: {}", request.getPhoneNumber());
            PasswordResetDto.ResetPasswordResponse response = passwordResetService.resetPassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("비밀번호 재설정 오류: ", e);
            return ResponseEntity.badRequest().body(
                new PasswordResetDto.ResetPasswordResponse("비밀번호 재설정 실패: " + e.getMessage(), false)
            );
        }
    }
} 