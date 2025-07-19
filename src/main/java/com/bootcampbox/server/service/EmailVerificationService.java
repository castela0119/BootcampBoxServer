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
        // 개발 환경에서는 모든 이메일을 인증된 것으로 처리
        String env = System.getenv("SPRING_PROFILES_ACTIVE");
        String sys = System.getProperty("spring.profiles.active", "");
        if ((env != null && env.contains("local")) || (sys != null && sys.contains("local"))) {
            return true;
        }
        return verifiedEmails.getOrDefault(email, false);
    }

    // ====== [개발용] 이메일 강제 인증 메서드 ======
    public void forceVerifyEmail(String email) {
        verifiedEmails.put(email, true);
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