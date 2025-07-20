package com.bootcampbox.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSchedulerService {
    
    private final NotificationService notificationService;
    
    // 매일 새벽 3시에 오래된 알림 정리 (30일 이상)
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOldNotifications() {
        log.info("=== 오래된 알림 정리 작업 시작 ===");
        try {
            notificationService.cleanupOldNotifications();
            log.info("=== 오래된 알림 정리 작업 완료 ===");
        } catch (Exception e) {
            log.error("오래된 알림 정리 작업 실패: ", e);
        }
    }
} 