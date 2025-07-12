package com.bootcampbox.server.config;

import com.bootcampbox.server.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    // 로그아웃된 토큰을 저장할 Set (실제 프로덕션에서는 Redis 사용 권장)
    private final Set<String> blacklistedTokens = new HashSet<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            
            // 블랙리스트 토큰 검증
            if (blacklistedTokens.contains(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }
            
            final String username = jwtUtil.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("JWT 토큰 처리 중 오류 발생", e);
        }

        filterChain.doFilter(request, response);
    }
    
    // 로그아웃 시 토큰을 블랙리스트에 추가하는 메서드
    public void addToBlacklist(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            blacklistedTokens.add(jwt);
        }
    }
    
    // 토큰이 블랙리스트에 있는지 확인하는 메서드
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
} 