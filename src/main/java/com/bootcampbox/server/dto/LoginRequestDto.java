package com.bootcampbox.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
    
    // 관리자 로그인 여부 (기본값: false)
    private boolean isAdminLogin = false;
} 