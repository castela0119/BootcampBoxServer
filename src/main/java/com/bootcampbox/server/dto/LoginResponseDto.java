package com.bootcampbox.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    private Long id;
    private String nickname;
    private String token;
    private String role; // 사용자 역할 (USER, ADMIN)
    private String roleType; // 관리자 등급 (SUPER_ADMIN, ADMIN, MANAGER, SUPPORT)
} 