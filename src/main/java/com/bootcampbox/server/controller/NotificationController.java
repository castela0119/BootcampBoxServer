package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.NotificationDto;
import com.bootcampbox.server.service.NotificationService;
import com.bootcampbox.server.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    private final WebSocketService webSocketService;

    // 알림 목록 조회 (페이징)
    @GetMapping
    public ResponseEntity<NotificationDto.NotificationListResponse> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("알림 목록 조회 요청: {}, 페이지: {}, 크기: {}", username, page, size);
        
        NotificationDto.NotificationListResponse response = notificationService.getMyNotifications(username, page, size);
        return ResponseEntity.ok(response);
    }

    // 읽지 않은 알림 개수 조회
    @GetMapping("/count")
    public ResponseEntity<NotificationDto.UnreadCountResponse> getUnreadCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("읽지 않은 알림 개수 조회 요청: {}", username);
        
        NotificationDto.UnreadCountResponse response = notificationService.getUnreadCount(username);
        return ResponseEntity.ok(response);
    }

    // 특정 알림 읽음 처리
    @PutMapping("/{id}")
    public ResponseEntity<NotificationDto.SimpleResponse> markAsRead(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("알림 읽음 처리 요청: {}, 알림 ID: {}", username, id);
        
        NotificationDto.SimpleResponse response = notificationService.markAsRead(username, id);
        
        // WebSocket으로 읽지 않은 알림 개수 업데이트 전송
        webSocketService.sendUnreadCountToUser(username);
        
        return ResponseEntity.ok(response);
    }

    // 모든 알림 읽음 처리
    @PutMapping("/read-all")
    public ResponseEntity<NotificationDto.SimpleResponse> markAllAsRead() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("모든 알림 읽음 처리 요청: {}", username);
        
        NotificationDto.SimpleResponse response = notificationService.markAllAsRead(username);
        
        // WebSocket으로 읽지 않은 알림 개수 업데이트 전송
        webSocketService.sendUnreadCountToUser(username);
        
        return ResponseEntity.ok(response);
    }

    // 알림 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<NotificationDto.SimpleResponse> deleteNotification(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("알림 삭제 요청: {}, 알림 ID: {}", username, id);
        
        NotificationDto.SimpleResponse response = notificationService.deleteNotification(username, id);
        return ResponseEntity.ok(response);
    }

    // 테스트 알림 발송
    @PostMapping("/test")
    public ResponseEntity<NotificationDto.SimpleResponse> sendTestNotification() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("테스트 알림 발송 요청: {}", username);
        
        NotificationDto.SimpleResponse response = notificationService.sendTestNotification(username);
        return ResponseEntity.ok(response);
    }
} 