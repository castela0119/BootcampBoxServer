package com.bootcampbox.server.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class EmailService {
    private static final int CODE_LENGTH = 6;
    private static final String CODE_CHARSET = "0123456789";
    private final Random random = new Random();

    public String generateVerificationCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_CHARSET.charAt(random.nextInt(CODE_CHARSET.length())));
        }
        return sb.toString();
    }

    public void sendVerificationCode(String email, String code) {
        // 실제 이메일 발송 대신 콘솔에 출력 (모의발송)
        System.out.printf("[MOCK EMAIL] To: %s, 인증코드: %s\n", email, code);
        // 로그 파일에도 출력
        System.err.printf("[MOCK EMAIL] To: %s, 인증코드: %s\n", email, code);
        // 파일에도 저장
        try {
            java.nio.file.Files.write(
                java.nio.file.Path.of("email_code.txt"), 
                String.format("[%s] To: %s, 인증코드: %s\n", 
                    java.time.LocalDateTime.now(), email, code).getBytes(),
                java.nio.file.StandardOpenOption.CREATE, 
                java.nio.file.StandardOpenOption.APPEND
            );
        } catch (Exception e) {
            System.err.println("인증코드 파일 저장 실패: " + e.getMessage());
        }
    }

    public void sendTemporaryPassword(String email, String temporaryPassword) {
        // 실제 이메일 발송 대신 콘솔에 출력 (모의발송)
        System.out.printf("[MOCK EMAIL] To: %s, 임시비밀번호: %s\n", email, temporaryPassword);
        // 로그 파일에도 출력
        System.err.printf("[MOCK EMAIL] To: %s, 임시비밀번호: %s\n", email, temporaryPassword);
        // 파일에도 저장
        try {
            java.nio.file.Files.write(
                java.nio.file.Path.of("email_code.txt"), 
                String.format("[%s] To: %s, 임시비밀번호: %s\n", 
                    java.time.LocalDateTime.now(), email, temporaryPassword).getBytes(),
                java.nio.file.StandardOpenOption.CREATE, 
                java.nio.file.StandardOpenOption.APPEND
            );
        } catch (Exception e) {
            System.err.println("임시비밀번호 파일 저장 실패: " + e.getMessage());
        }
    }
} 