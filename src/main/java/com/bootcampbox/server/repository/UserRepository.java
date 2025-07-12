package com.bootcampbox.server.repository;

import com.bootcampbox.server.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
    
    // 관리자 기능을 위한 메서드들
    Page<User> findByUsernameContainingOrNicknameContaining(String username, String nickname, Pageable pageable);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
} 