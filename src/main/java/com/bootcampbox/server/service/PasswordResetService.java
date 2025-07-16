package com.bootcampbox.server.service;

import com.bootcampbox.server.dto.PasswordResetDto;
import com.bootcampbox.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;

    // 1단계: 비밀번호 찾기 인증코드 발송
    public PasswordResetDto.ResetPasswordResponse sendResetCode(PasswordResetDto.SendResetCodeRequest request) {
        // 이메일로 가입된 사용자가 있는지 확인
        if (!userRepository.existsByEmail(request.getEmail())) {
            return new PasswordResetDto.ResetPasswordResponse("해당 이메일로 가입된 사용자가 없습니다.", false);
        }

        // 인증코드 생성 및 발송
        String verificationCode = emailService.generateVerificationCode();
        emailService.sendVerificationCode(request.getEmail(), verificationCode);
        emailVerificationService.saveVerificationCode(request.getEmail(), verificationCode);

        return new PasswordResetDto.ResetPasswordResponse(
            "비밀번호 찾기 인증코드가 이메일로 발송되었습니다.", 
            true
        );
    }

    // 2단계: 인증코드 검증 및 임시비밀번호 발급
    @Transactional
    public PasswordResetDto.ResetPasswordResponse verifyResetCode(PasswordResetDto.VerifyResetCodeRequest request) {
        // 이메일로 가입된 사용자가 있는지 확인
        if (!userRepository.existsByEmail(request.getEmail())) {
            return new PasswordResetDto.ResetPasswordResponse("해당 이메일로 가입된 사용자가 없습니다.", false);
        }

        // 인증코드 검증
        if (!emailVerificationService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            return new PasswordResetDto.ResetPasswordResponse("인증코드가 올바르지 않거나 만료되었습니다.", false);
        }

        // 임시비밀번호 생성
        String temporaryPassword = generateTemporaryPassword();
        
        // 사용자 비밀번호를 임시비밀번호로 변경
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(temporaryPassword));
            userRepository.save(user);
        });

        // 임시비밀번호를 이메일로 발송
        emailService.sendTemporaryPassword(request.getEmail(), temporaryPassword);

        return new PasswordResetDto.ResetPasswordResponse(
            "인증이 완료되었습니다. 임시비밀번호가 이메일로 발송되었습니다.", 
            true,
            temporaryPassword // 개발/테스트용으로 응답에도 포함
        );
    }

    // 3단계: 새 비밀번호 설정 (선택사항)
    @Transactional
    public PasswordResetDto.ResetPasswordResponse setNewPassword(PasswordResetDto.SetNewPasswordRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    userRepository.save(user);
                    return new PasswordResetDto.ResetPasswordResponse("비밀번호가 성공적으로 변경되었습니다.", true);
                })
                .orElseGet(() -> new PasswordResetDto.ResetPasswordResponse("해당 이메일로 가입된 사용자가 없습니다.", false));
    }

    // 임시비밀번호 생성
    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        // 최소 8자, 영문자, 숫자, 특수문자 포함
        sb.append((char) ('A' + random.nextInt(26))); // 대문자
        sb.append((char) ('a' + random.nextInt(26))); // 소문자
        sb.append((char) ('0' + random.nextInt(10))); // 숫자
        sb.append("!@#$%^&*".charAt(random.nextInt(8))); // 특수문자
        
        // 나머지 4자 랜덤
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // 문자열을 섞기
        char[] passwordArray = sb.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }
} 