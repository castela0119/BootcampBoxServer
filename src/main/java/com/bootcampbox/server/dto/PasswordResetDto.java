package com.bootcampbox.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

public class PasswordResetDto {

    // 1단계: 이메일 입력 (인증코드 발송)
    @Getter
    @Setter
    public static class SendResetCodeRequest {
        @NotBlank(message = "이메일은 필수입니다.")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", 
                message = "이메일 형식이 올바르지 않습니다.")
        private String email;
    }

    // 2단계: 인증코드 검증 (임시비밀번호 발급)
    @Getter
    @Setter
    public static class VerifyResetCodeRequest {
        @NotBlank(message = "이메일은 필수입니다.")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", 
                message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "인증코드는 필수입니다.")
        private String verificationCode;
    }

    // 3단계: 새 비밀번호 설정 (선택사항)
    @Getter
    @Setter
    public static class SetNewPasswordRequest {
        @NotBlank(message = "이메일은 필수입니다.")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", 
                message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상, 영문자, 숫자, 특수문자를 포함해야 합니다.")
        private String newPassword;
    }

    // 공통 응답
    @Getter
    @Setter
    public static class ResetPasswordResponse {
        private String message;
        private boolean success;
        private String temporaryPassword; // 임시비밀번호 (인증코드 검증 성공 시)

        public ResetPasswordResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }

        public ResetPasswordResponse(String message, boolean success, String temporaryPassword) {
            this.message = message;
            this.success = success;
            this.temporaryPassword = temporaryPassword;
        }
    }
} 