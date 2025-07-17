package com.bootcampbox.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.bootcampbox.server.repository.UserRepository;
import com.bootcampbox.server.domain.User;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Profile("!prod")
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final AdminSecurityConfig adminSecurityConfig;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    String email = null;
                    try {
                        if ("POST".equals(request.getMethod()) && "/api/auth/login".equals(request.getRequestURI())) {
                            StringBuilder sb = new StringBuilder();
                            BufferedReader reader = request.getReader();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                            String body = sb.toString();
                            if (!body.isEmpty()) {
                                JsonNode jsonNode = objectMapper.readTree(body);
                                if (jsonNode.has("email")) {
                                    email = jsonNode.get("email").asText();
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 무시
                    }

                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    if (email != null && userRepository.findByEmail(email).isPresent()) {
                        User user = userRepository.findByEmail(email).get();
                        if ("ADMIN".equals(user.getRole())) {
                            errorResponse.put("message", "관리자 계정의 비밀번호가 올바르지 않습니다. 다시 확인해주세요.");
                            errorResponse.put("error", "AdminLoginException");
                        } else {
                            errorResponse.put("message", "이메일 또는 비밀번호가 올바르지 않습니다.");
                            errorResponse.put("error", "BadCredentialsException");
                        }
                    } else {
                        errorResponse.put("message", "이메일 또는 비밀번호가 올바르지 않습니다.");
                        errorResponse.put("error", "BadCredentialsException");
                    }
                    errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
                    objectMapper.writeValue(response.getWriter(), errorResponse);
                })
            );
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