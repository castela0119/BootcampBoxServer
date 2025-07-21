package com.bootcampbox.server.config;

import com.bootcampbox.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketSecurityConfig implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                
                try {
                    String username = jwtUtil.extractUsername(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    if (jwtUtil.validateToken(token, userDetails)) {
                        // 사용자 정보를 WebSocket 세션에 저장
                        accessor.setUser(() -> username);
                        log.info("WebSocket 연결 인증 성공 - 사용자: {}", username);
                    } else {
                        log.warn("WebSocket 연결 인증 실패 - 토큰이 유효하지 않음");
                        return null;
                    }
                } catch (Exception e) {
                    log.error("WebSocket 연결 인증 오류: ", e);
                    return null;
                }
            } else {
                log.warn("WebSocket 연결 인증 실패 - 토큰이 없음");
                return null;
            }
        }
        
        return message;
    }
} 