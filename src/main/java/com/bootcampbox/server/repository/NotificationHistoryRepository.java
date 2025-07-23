package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.NotificationHistory;
import com.bootcampbox.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {

    /**
     * 특정 사용자-게시글 조합의 좋아요 알림 이력 조회
     */
    @Query("SELECT nh FROM NotificationHistory nh " +
           "WHERE nh.recipient.id = :recipientId " +
           "AND nh.sender.id = :senderId " +
           "AND nh.notificationType = 'like' " +
           "AND nh.targetType = 'post' " +
           "AND nh.targetId = :targetId")
    Optional<NotificationHistory> findLikeHistory(
            @Param("recipientId") Long recipientId,
            @Param("senderId") Long senderId,
            @Param("targetId") Long targetId);

    /**
     * 특정 사용자-게시글 조합의 댓글 알림 이력 조회
     */
    @Query("SELECT nh FROM NotificationHistory nh " +
           "WHERE nh.recipient.id = :recipientId " +
           "AND nh.sender.id = :senderId " +
           "AND nh.notificationType = 'comment' " +
           "AND nh.targetType = 'post' " +
           "AND nh.targetId = :targetId")
    Optional<NotificationHistory> findCommentHistory(
            @Param("recipientId") Long recipientId,
            @Param("senderId") Long senderId,
            @Param("targetId") Long targetId);

    /**
     * 특정 시간 내에 같은 사용자-게시글 조합의 좋아요 알림 이력 존재 여부 확인
     */
    @Query("SELECT COUNT(nh) > 0 FROM NotificationHistory nh " +
           "WHERE nh.recipient.id = :recipientId " +
           "AND nh.sender.id = :senderId " +
           "AND nh.notificationType = 'like' " +
           "AND nh.targetType = 'post' " +
           "AND nh.targetId = :targetId " +
           "AND nh.sentAt >= :since")
    boolean existsLikeHistoryWithinTime(
            @Param("recipientId") Long recipientId,
            @Param("senderId") Long senderId,
            @Param("targetId") Long targetId,
            @Param("since") LocalDateTime since);

    /**
     * 특정 시간 내에 같은 사용자-댓글 조합의 좋아요 알림 이력 존재 여부 확인
     */
    @Query("SELECT COUNT(nh) > 0 FROM NotificationHistory nh " +
           "WHERE nh.recipient.id = :recipientId " +
           "AND nh.sender.id = :senderId " +
           "AND nh.notificationType = 'like' " +
           "AND nh.targetType = 'comment' " +
           "AND nh.targetId = :targetId " +
           "AND nh.sentAt >= :since")
    boolean existsCommentLikeHistoryWithinTime(
            @Param("recipientId") Long recipientId,
            @Param("senderId") Long senderId,
            @Param("targetId") Long targetId,
            @Param("since") LocalDateTime since);

    /**
     * 특정 시간 내에 같은 사용자-게시글 조합의 댓글 알림 이력 존재 여부 확인
     */
    @Query("SELECT COUNT(nh) > 0 FROM NotificationHistory nh " +
           "WHERE nh.recipient.id = :recipientId " +
           "AND nh.sender.id = :senderId " +
           "AND nh.notificationType = 'comment' " +
           "AND nh.targetType = 'post' " +
           "AND nh.targetId = :targetId " +
           "AND nh.sentAt >= :since")
    boolean existsCommentHistoryWithinTime(
            @Param("recipientId") Long recipientId,
            @Param("senderId") Long senderId,
            @Param("targetId") Long targetId,
            @Param("since") LocalDateTime since);

    /**
     * 오래된 알림 이력 삭제 (30일 이상)
     */
    @Modifying
    @Query("DELETE FROM NotificationHistory nh WHERE nh.sentAt < :cutoffDate")
    void deleteOldHistory(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 특정 게시글의 모든 알림 이력 삭제 (게시글 삭제 시)
     */
    @Modifying
    @Query("DELETE FROM NotificationHistory nh " +
           "WHERE nh.targetType = 'post' AND nh.targetId = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    /**
     * 특정 사용자의 모든 알림 이력 삭제 (사용자 삭제 시)
     */
    @Modifying
    @Query("DELETE FROM NotificationHistory nh " +
           "WHERE nh.recipient.id = :userId OR nh.sender.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
} 