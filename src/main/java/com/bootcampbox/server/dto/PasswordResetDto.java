package com.bootcampbox.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

public class PasswordResetDto {

    @Getter
    @Setter
    public static class ResetPasswordRequest {
        @NotBlank(message = "휴대폰 번호는 필수입니다.")
        @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
        private String phoneNumber;

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상, 영문자, 숫자, 특수문자를 포함해야 합니다.")
        private String newPassword;
    }

    @Getter
    @Setter
    public static class ResetPasswordResponse {
        private String message;
        private boolean success;

        public ResetPasswordResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }
    }
} 