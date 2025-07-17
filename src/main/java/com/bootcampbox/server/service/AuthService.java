package com.bootcampbox.server.service;

import com.bootcampbox.server.config.JwtAuthenticationFilter;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.LoginRequestDto;
import com.bootcampbox.server.dto.LoginResponseDto;
import com.bootcampbox.server.exception.AdminLoginException;
import com.bootcampbox.server.repository.UserRepository;
import com.bootcampbox.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public LoginResponseDto login(LoginRequestDto loginRequest) {
        try {
            // Spring Security를 통한 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            // 사용자 정보 조회
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            return new LoginResponseDto(
                    user.getId(),
                    user.getNickname(),
                    token,
                    user.getRole(), // 역할 정보
                    user.getRoleType() != null ? user.getRoleType().name() : null // 관리자 등급 정보
            );
        } catch (BadCredentialsException e) {
            // 비밀번호 틀림 시 구체적 메시지
            if (userRepository.findByEmail(loginRequest.getEmail()).isPresent()) {
                User user = userRepository.findByEmail(loginRequest.getEmail()).get();
                if ("ADMIN".equals(user.getRole())) {
                    throw new AdminLoginException("관리자 계정의 비밀번호가 올바르지 않습니다. 다시 확인해주세요.");
                } else {
                    throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
                }
            } else {
                throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
            }
        }
    }

    public void logout(String token) {
        jwtAuthenticationFilter.addToBlacklist(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return jwtAuthenticationFilter.isTokenBlacklisted(token);
    }
} 