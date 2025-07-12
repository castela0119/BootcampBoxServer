package com.bootcampbox.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

public class DuplicateCheckDto {

    @Getter
    @Setter
    public static class CheckRequest {
        @NotBlank(message = "확인할 값은 필수입니다.")
        private String value;
        
        @NotBlank(message = "확인 타입은 필수입니다.")
        @Pattern(regexp = "^(email|nickname)$", message = "확인 타입은 email 또는 nickname이어야 합니다.")
        private String type; // email 또는 nickname
    }

    @Getter
    @Setter
    public static class CheckResponse {
        private String message;
        private boolean isDuplicate;
        private boolean available;

        public CheckResponse(String message, boolean isDuplicate) {
            this.message = message;
            this.isDuplicate = isDuplicate;
            this.available = !isDuplicate;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class EmailVerificationRequest {
        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class EmailCodeVerifyRequest {
        private String email;
        private String code;
    }
} 