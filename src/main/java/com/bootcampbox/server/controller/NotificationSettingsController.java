package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.NotificationSettingsDto;
import com.bootcampbox.server.service.NotificationSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications/settings")
@RequiredArgsConstructor
@Slf4j
public class NotificationSettingsController {
    
    private final NotificationSettingsService settingsService;
    
    // 알림 설정 조회
    @GetMapping
    public ResponseEntity<NotificationSettingsDto.ApiResponse> getSettings() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            log.info("알림 설정 조회 요청: {}", username);
            
            NotificationSettingsDto.SettingsResponse settings = settingsService.getUserSettings(username);
            
            return ResponseEntity.ok(NotificationSettingsDto.ApiResponse.success(
                "알림 설정을 성공적으로 조회했습니다.",
                settings
            ));
        } catch (Exception e) {
            log.error("알림 설정 조회 실패: ", e);
            return ResponseEntity.internalServerError().body(
                NotificationSettingsDto.ApiResponse.error(
                    "알림 설정 조회에 실패했습니다.",
                    Map.of("code", "INTERNAL_SERVER_ERROR", "details", e.getMessage())
                )
            );
        }
    }
    
    // 알림 설정 전체 업데이트
    @PutMapping
    public ResponseEntity<NotificationSettingsDto.ApiResponse> updateSettings(
            @RequestBody NotificationSettingsDto.SettingsRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            log.info("알림 설정 업데이트 요청: {}, 설정: {}", username, request.getSettings());
            
            NotificationSettingsDto.SettingsResponse settings = settingsService.updateSettings(username, request.getSettings());
            
            return ResponseEntity.ok(NotificationSettingsDto.ApiResponse.success(
                "알림 설정이 업데이트되었습니다.",
                settings
            ));
        } catch (IllegalArgumentException e) {
            log.warn("알림 설정 업데이트 실패 (잘못된 요청): {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                NotificationSettingsDto.ApiResponse.error(
                    "잘못된 요청입니다.",
                    Map.of("code", "INVALID_SETTINGS", "details", e.getMessage())
                )
            );
        } catch (Exception e) {
            log.error("알림 설정 업데이트 실패: ", e);
            return ResponseEntity.internalServerError().body(
                NotificationSettingsDto.ApiResponse.error(
                    "알림 설정 업데이트에 실패했습니다.",
                    Map.of("code", "INTERNAL_SERVER_ERROR", "details", e.getMessage())
                )
            );
        }
    }
    
    // 특정 알림 타입 설정 업데이트
    @PutMapping("/{type}")
    public ResponseEntity<NotificationSettingsDto.ApiResponse> updateTypeSetting(
            @PathVariable String type,
            @RequestBody NotificationSettingsDto.TypeSettingRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            log.info("알림 타입 설정 업데이트 요청: {}, 타입: {}, 활성화: {}", username, type, request.isEnabled());
            
            NotificationSettingsDto.TypeSettingResponse response = settingsService.updateTypeSetting(username, type, request.isEnabled());
            
            String typeDisplayName = settingsService.getTypeDisplayName(type);
            
            return ResponseEntity.ok(NotificationSettingsDto.ApiResponse.success(
                typeDisplayName + " 알림 설정이 업데이트되었습니다.",
                response
            ));
        } catch (IllegalArgumentException e) {
            log.warn("알림 타입 설정 업데이트 실패 (잘못된 요청): {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                NotificationSettingsDto.ApiResponse.error(
                    "잘못된 요청입니다.",
                    Map.of("code", "INVALID_SETTINGS", "details", e.getMessage())
                )
            );
        } catch (Exception e) {
            log.error("알림 타입 설정 업데이트 실패: ", e);
            return ResponseEntity.internalServerError().body(
                NotificationSettingsDto.ApiResponse.error(
                    "알림 설정 업데이트에 실패했습니다.",
                    Map.of("code", "INTERNAL_SERVER_ERROR", "details", e.getMessage())
                )
            );
        }
    }
    
    // 알림 설정 초기화
    @PostMapping("/reset")
    public ResponseEntity<NotificationSettingsDto.ApiResponse> resetSettings() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            log.info("알림 설정 초기화 요청: {}", username);
            
            NotificationSettingsDto.SettingsResponse settings = settingsService.resetToDefault(username);
            
            return ResponseEntity.ok(NotificationSettingsDto.ApiResponse.success(
                "알림 설정이 기본값으로 초기화되었습니다.",
                settings
            ));
        } catch (Exception e) {
            log.error("알림 설정 초기화 실패: ", e);
            return ResponseEntity.internalServerError().body(
                NotificationSettingsDto.ApiResponse.error(
                    "알림 설정 초기화에 실패했습니다.",
                    Map.of("code", "INTERNAL_SERVER_ERROR", "details", e.getMessage())
                )
            );
        }
    }
} 