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

    // 1단계: 비밀번호 찾기 인증코드 발송
    @PostMapping("/send-reset-code")
    public ResponseEntity<PasswordResetDto.ResetPasswordResponse> sendResetCode(
            @Valid @RequestBody PasswordResetDto.SendResetCodeRequest request) {
        try {
            log.info("비밀번호 찾기 인증코드 발송 요청: {}", request.getEmail());
            PasswordResetDto.ResetPasswordResponse response = passwordResetService.sendResetCode(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("비밀번호 찾기 인증코드 발송 오류: ", e);
            return ResponseEntity.badRequest().body(
                new PasswordResetDto.ResetPasswordResponse("인증코드 발송 실패: " + e.getMessage(), false)
            );
        }
    }

    // 2단계: 인증코드 검증 및 임시비밀번호 발급
    @PostMapping("/verify-reset-code")
    public ResponseEntity<PasswordResetDto.ResetPasswordResponse> verifyResetCode(
            @Valid @RequestBody PasswordResetDto.VerifyResetCodeRequest request) {
        try {
            log.info("비밀번호 찾기 인증코드 검증 요청: {}", request.getEmail());
            PasswordResetDto.ResetPasswordResponse response = passwordResetService.verifyResetCode(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("비밀번호 찾기 인증코드 검증 오류: ", e);
            return ResponseEntity.badRequest().body(
                new PasswordResetDto.ResetPasswordResponse("인증코드 검증 실패: " + e.getMessage(), false)
            );
        }
    }

    // 3단계: 새 비밀번호 설정 (선택사항)
    @PostMapping("/set-new-password")
    public ResponseEntity<PasswordResetDto.ResetPasswordResponse> setNewPassword(
            @Valid @RequestBody PasswordResetDto.SetNewPasswordRequest request) {
        try {
            log.info("새 비밀번호 설정 요청: {}", request.getEmail());
            PasswordResetDto.ResetPasswordResponse response = passwordResetService.setNewPassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("새 비밀번호 설정 오류: ", e);
            return ResponseEntity.badRequest().body(
                new PasswordResetDto.ResetPasswordResponse("비밀번호 설정 실패: " + e.getMessage(), false)
            );
        }
    }

    // 기존 API (하위 호환성을 위해 유지)
    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetDto.ResetPasswordResponse> resetPassword(
            @Valid @RequestBody PasswordResetDto.SetNewPasswordRequest request) {
        try {
            log.info("비밀번호 재설정 요청: {}", request.getEmail());
            PasswordResetDto.ResetPasswordResponse response = passwordResetService.setNewPassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("비밀번호 재설정 오류: ", e);
            return ResponseEntity.badRequest().body(
                new PasswordResetDto.ResetPasswordResponse("비밀번호 재설정 실패: " + e.getMessage(), false)
            );
        }
    }
} 