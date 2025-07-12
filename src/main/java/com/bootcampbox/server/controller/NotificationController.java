package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.NotificationDto;
import com.bootcampbox.server.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationDto.NotificationListResponse> getMyNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("알림 목록 조회 요청: {}", username);
        NotificationDto.NotificationListResponse response = notificationService.getMyNotifications(username);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/read")
    public ResponseEntity<NotificationDto.SimpleResponse> markAllAsRead() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("알림 읽음 처리 요청: {}", username);
        NotificationDto.SimpleResponse response = notificationService.markAllAsRead(username);
        return ResponseEntity.ok(response);
    }
} 