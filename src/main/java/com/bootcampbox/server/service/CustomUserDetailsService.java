package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        String role = user.getRole() != null ? user.getRole() : "USER";
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        String userEmail = user.getEmail();
        String password = user.getPassword();

        if (userEmail == null || userEmail.isBlank() || password == null || password.isBlank() || authorities == null) {
            throw new UsernameNotFoundException("User details contain null or empty values");
        }

        // 값 로그 출력 (개발용, 실제 서비스 배포 전에는 반드시 제거)
        log.info("UserDetails 생성 - email: {}, password: {}, authorities: {}", userEmail, password, authorities);

        return new org.springframework.security.core.userdetails.User(
                userEmail,
                password,
                authorities
        );
    }
} 