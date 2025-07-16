package com.bootcampbox.server.dto;

import com.bootcampbox.server.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

public class UserDto {

    @Getter
    @NoArgsConstructor
    public static class SignUpRequest {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        // 휴대폰 번호
        //@NotBlank(message = "휴대폰 번호는 필수 입력값입니다.")
        @Pattern(regexp = "^$|^\\d{3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
        private String phoneNumber;

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        private String nickname;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상, 영문자, 숫자, 특수문자를 포함해야 합니다.")
        private String password;

        @NotBlank(message = "사용자 타입은 필수 입력값입니다.")
        private String userType; // 현역, 예비역, 곰신, 민간인, 기타

        private String zipcode;
        private String address;
        private String addressDetail;

        // 군 복무 정보
        private String rank;
        private String branch;
        private String enlistDate;
        private String dischargeDate;
        private String unit;

        // 곰신 정보
        private String boyfriendRank;
        private String boyfriendBranch;
        private String boyfriendEnlistDate;
        private String boyfriendDischargeDate;
        private String boyfriendUnit;

        @Builder
        public SignUpRequest(String email, String phoneNumber, String nickname, String password, String userType,
                           String zipcode, String address, String addressDetail,
                           String rank, String branch, String enlistDate, String dischargeDate, String unit,
                           String boyfriendRank, String boyfriendBranch, String boyfriendEnlistDate, 
                           String boyfriendDischargeDate, String boyfriendUnit) {
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.nickname = nickname;
            this.password = password;
            this.userType = userType;
            this.zipcode = zipcode;
            this.address = address;
            this.addressDetail = addressDetail;
            this.rank = rank;
            this.branch = branch;
            this.enlistDate = enlistDate;
            this.dischargeDate = dischargeDate;
            this.unit = unit;
            this.boyfriendRank = boyfriendRank;
            this.boyfriendBranch = boyfriendBranch;
            this.boyfriendEnlistDate = boyfriendEnlistDate;
            this.boyfriendDischargeDate = boyfriendDischargeDate;
            this.boyfriendUnit = boyfriendUnit;
        }
    }

    @Getter
    @Builder
    public static class SignUpResponse {
        private Long id;
        private String phoneNumber;
        private String nickname;
        private String userType;
        private String role;

        public static SignUpResponse from(User user) {
            return SignUpResponse.builder()
                    .id(user.getId())
                    .phoneNumber(user.getPhoneNumber())
                    .nickname(user.getNickname())
                    .userType(user.getUserType())
                    .role(user.getRole())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SimpleUserResponse {
        private Long id;
        private String nickname;
        private String userType;

        public static SimpleUserResponse from(User user) {
            return SimpleUserResponse.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .userType(user.getUserType())
                    .build();
        }
    }

    // === 공통 응답 ===
    @Getter
    @Setter
    @AllArgsConstructor
    public static class SimpleResponse {
        private String message;
        private boolean success;
    }

    // === 관리자 회원가입 요청 ===
    @Getter
    @NoArgsConstructor
    public static class AdminSignUpRequest {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        private String nickname;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상, 영문자, 숫자, 특수문자를 포함해야 합니다.")
        private String password;

        @NotBlank(message = "관리자 등급은 필수 입력값입니다.")
        private String roleType; // SUPER_ADMIN, ADMIN, MANAGER, SUPPORT

        @Pattern(regexp = "^$|^\\d{3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
        private String phoneNumber;

        private String zipcode;
        private String address;
        private String addressDetail;
    }
} 