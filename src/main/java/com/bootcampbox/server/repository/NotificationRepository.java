package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.Notification;
import com.bootcampbox.server.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // 사용자별 알림 목록 (페이징)
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    // 사용자별 읽지 않은 알림 목록
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    
    // 사용자별 읽지 않은 알림 개수
    long countByUserAndIsReadFalse(User user);
    
    // 특정 알림 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.user = :user")
    void markAsRead(@Param("id") Long id, @Param("user") User user);
    
    // 사용자의 모든 알림 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user")
    void markAllAsRead(@Param("user") User user);
    
    // 오래된 알림 삭제 (30일 이상)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
} 