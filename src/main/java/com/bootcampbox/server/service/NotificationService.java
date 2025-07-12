package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.Notification;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.NotificationDto;
import com.bootcampbox.server.repository.NotificationRepository;
import com.bootcampbox.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationDto.NotificationListResponse getMyNotifications(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return NotificationDto.NotificationListResponse.from(notifications);
    }

    @Transactional
    public NotificationDto.SimpleResponse markAllAsRead(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
        return new NotificationDto.SimpleResponse("모든 알림을 읽음 처리했습니다.", true);
    }

    // 알림 생성 (다른 서비스에서 호출)
    @Transactional
    public void sendNotification(User user, String content) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setContent(content);
        notification.setRead(false);
        notificationRepository.save(notification);
    }
} 