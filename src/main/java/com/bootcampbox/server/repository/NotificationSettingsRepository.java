package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.NotificationSettings;
import com.bootcampbox.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
    
    // 사용자별 알림 설정 조회
    Optional<NotificationSettings> findByUser(User user);
    
    // 사용자 ID로 알림 설정 조회
    Optional<NotificationSettings> findByUserId(Long userId);
    
    // 사용자별 알림 설정 존재 여부 확인
    boolean existsByUser(User user);
    
    // 사용자 ID로 알림 설정 존재 여부 확인
    boolean existsByUserId(Long userId);
} 