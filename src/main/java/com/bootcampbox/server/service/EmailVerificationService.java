package com.bootcampbox.server.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailVerificationService {
    private static final int CODE_EXPIRY_MINUTES = 5;
    private final Map<String, VerificationInfo> verificationMap = new ConcurrentHashMap<>();
    private final Map<String, Boolean> verifiedEmails = new ConcurrentHashMap<>();

    public void saveVerificationCode(String email, String code) {
        verificationMap.put(email, new VerificationInfo(code, LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES)));
        // 기존 인증 상태 초기화
        verifiedEmails.remove(email);
    }

    public boolean verifyCode(String email, String code) {
        VerificationInfo info = verificationMap.get(email);
        if (info == null) return false;
        if (info.expiry.isBefore(LocalDateTime.now())) {
            verificationMap.remove(email);
            return false;
        }
        boolean result = info.code.equals(code);
        if (result) {
            verificationMap.remove(email);
            verifiedEmails.put(email, true); // 인증 완료 표시
        }
        return result;
    }

    public boolean isEmailVerified(String email) {
        return verifiedEmails.getOrDefault(email, false);
    }

    private static class VerificationInfo {
        String code;
        LocalDateTime expiry;
        VerificationInfo(String code, LocalDateTime expiry) {
            this.code = code;
            this.expiry = expiry;
        }
    }
} 