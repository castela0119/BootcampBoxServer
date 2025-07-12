package com.bootcampbox.server.service;

import com.bootcampbox.server.config.JwtAuthenticationFilter;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.LoginRequestDto;
import com.bootcampbox.server.dto.LoginResponseDto;
import com.bootcampbox.server.repository.UserRepository;
import com.bootcampbox.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
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
                token
        );
    }

    public void logout(String token) {
        jwtAuthenticationFilter.addToBlacklist(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return jwtAuthenticationFilter.isTokenBlacklisted(token);
    }
} 