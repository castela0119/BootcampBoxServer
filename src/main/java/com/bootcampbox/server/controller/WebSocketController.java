package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.WebSocketMessageDto;
import com.bootcampbox.server.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final WebSocketService webSocketService;

    /**
     * 클라이언트로부터 연결 확인 메시지 수신
     */
    @MessageMapping("/connect")
    @SendToUser("/queue/connection")
    public WebSocketMessageDto.ConnectionMessage handleConnection(String username) {
        log.info("WebSocket 연결 확인 - 사용자: {}", username);
        
        return WebSocketMessageDto.ConnectionMessage.builder()
                .status("connected")
                .username(username)
                .build();
    }

    /**
     * 클라이언트로부터 메시지 수신 (에코 테스트용)
     */
    @MessageMapping("/echo")
    @SendTo("/topic/echo")
    public String handleEcho(String message) {
        log.info("WebSocket 에코 메시지 수신: {}", message);
        return "서버 응답: " + message;
    }
} 