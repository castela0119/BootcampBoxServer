package com.bootcampbox.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Profile("prod")
public class SecurityConfigProd {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // 인증/회원가입/게시글 조회는 모두 허용
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/users/signup").permitAll()
                .requestMatchers("/api/posts").permitAll()
                .requestMatchers("/api/posts/{id}").permitAll()
                .requestMatchers("/api/posts/{postId}/comments").permitAll()
                // 댓글 작성/수정/삭제/좋아요/신고는 로그인 필요
                .requestMatchers("/api/posts/comments/{commentId}/like").authenticated()
                .requestMatchers("/api/posts/comments/{commentId}/report").authenticated()
                .requestMatchers("/api/posts/comments/{commentId}").authenticated()
                // 좋아요/신고 내역 조회, 신고 취소는 관리자만
                .requestMatchers("/api/posts/comments/{commentId}/likes").hasRole("ADMIN")
                .requestMatchers("/api/posts/comments/{commentId}/reports").hasRole("ADMIN")
                .requestMatchers("/api/posts/comments/{commentId}/report/{userId}").hasRole("ADMIN")
                // 관리자 API는 ADMIN 역할 필요 (운영 환경)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 마이페이지, 내 댓글 등은 로그인 필요
                .requestMatchers("/api/user/**").authenticated()
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 