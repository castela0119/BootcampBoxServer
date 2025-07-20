package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.NotificationSettings;
import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.NotificationSettingsDto;
import com.bootcampbox.server.repository.NotificationSettingsRepository;
import com.bootcampbox.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSettingsService {
    
    private final NotificationSettingsRepository settingsRepository;
    private final UserRepository userRepository;
    
    // 사용자별 알림 설정 조회
    public NotificationSettingsDto.SettingsResponse getUserSettings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        NotificationSettings settings = settingsRepository.findByUser(user)
                .orElseGet(() -> createDefaultSettings(user));
        
        return NotificationSettingsDto.SettingsResponse.from(settings);
    }
    
    // 알림 설정 전체 업데이트
    @Transactional
    public NotificationSettingsDto.SettingsResponse updateSettings(String username, Map<String, Boolean> settingsMap) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        NotificationSettings settings = settingsRepository.findByUser(user)
                .orElseGet(() -> createDefaultSettings(user));
        
        // 설정 유효성 검사
        validateSettings(settingsMap);
        
        // 설정 업데이트
        settings.updateSettings(
                settingsMap.getOrDefault("comment", true),
                settingsMap.getOrDefault("like", true),
                settingsMap.getOrDefault("follow", true),
                settingsMap.getOrDefault("system", true)
        );
        
        settingsRepository.save(settings);
        
        log.info("알림 설정 업데이트 - 사용자: {}, 설정: {}", username, settingsMap);
        
        return NotificationSettingsDto.SettingsResponse.from(settings);
    }
    
    // 특정 알림 타입 설정 업데이트
    @Transactional
    public NotificationSettingsDto.TypeSettingResponse updateTypeSetting(String username, String type, boolean enabled) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        NotificationSettings settings = settingsRepository.findByUser(user)
                .orElseGet(() -> createDefaultSettings(user));
        
        // 타입 유효성 검사
        validateNotificationType(type);
        
        // 설정 업데이트
        settings.updateTypeSetting(type, enabled);
        settingsRepository.save(settings);
        
        log.info("알림 타입 설정 업데이트 - 사용자: {}, 타입: {}, 활성화: {}", username, type, enabled);
        
        return NotificationSettingsDto.TypeSettingResponse.from(settings, type);
    }
    
    // 알림 설정 초기화
    @Transactional
    public NotificationSettingsDto.SettingsResponse resetToDefault(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        NotificationSettings settings = settingsRepository.findByUser(user)
                .orElseGet(() -> createDefaultSettings(user));
        
        // 기본값으로 초기화
        settings.updateSettings(true, true, true, true);
        settingsRepository.save(settings);
        
        log.info("알림 설정 초기화 - 사용자: {}", username);
        
        return NotificationSettingsDto.SettingsResponse.from(settings);
    }
    
    // 알림 설정 확인 (알림 발송 시 사용)
    public boolean isNotificationEnabled(Long userId, String type) {
        NotificationSettings settings = settingsRepository.findByUserId(userId)
                .orElse(null);
        
        // 설정이 없으면 기본적으로 활성화
        if (settings == null) {
            return true;
        }
        
        switch (type.toLowerCase()) {
            case "comment":
                return settings.isCommentEnabled();
            case "like":
                return settings.isLikeEnabled();
            case "follow":
                return settings.isFollowEnabled();
            case "system":
                return settings.isSystemEnabled();
            default:
                return true; // 알 수 없는 타입은 기본적으로 활성화
        }
    }
    
    // 기본 설정 생성
    private NotificationSettings createDefaultSettings(User user) {
        NotificationSettings settings = NotificationSettings.createDefaultSettings(user);
        settingsRepository.save(settings);
        log.info("기본 알림 설정 생성 - 사용자: {}", user.getUsername());
        return settings;
    }
    
    // 설정 유효성 검사
    private void validateSettings(Map<String, Boolean> settings) {
        if (settings == null) {
            throw new IllegalArgumentException("설정 정보가 없습니다.");
        }
        
        // 필수 설정 확인
        String[] requiredTypes = {"comment", "like", "follow", "system"};
        for (String type : requiredTypes) {
            if (!settings.containsKey(type)) {
                throw new IllegalArgumentException("필수 설정이 누락되었습니다: " + type);
            }
        }
    }
    
    // 알림 타입 유효성 검사
    private void validateNotificationType(String type) {
        String[] validTypes = {"comment", "like", "follow", "system"};
        boolean isValid = false;
        
        for (String validType : validTypes) {
            if (validType.equals(type.toLowerCase())) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new IllegalArgumentException("유효하지 않은 알림 타입입니다: " + type);
        }
    }
    
    // 타입별 표시명 반환
    public String getTypeDisplayName(String type) {
        switch (type.toLowerCase()) {
            case "comment": return "댓글";
            case "like": return "좋아요";
            case "follow": return "팔로우";
            case "system": return "시스템";
            default: return type;
        }
    }
} 