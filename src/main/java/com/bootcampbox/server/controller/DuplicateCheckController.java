package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.DuplicateCheckDto;
import com.bootcampbox.server.service.DuplicateCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class DuplicateCheckController {

    private final DuplicateCheckService duplicateCheckService;

    @PostMapping("/check-duplicate")
    public ResponseEntity<Map<String, Object>> checkDuplicate(
            @Valid @RequestBody DuplicateCheckDto.CheckRequest request) {
        try {
            log.info("=== 중복확인 요청 시작 ===");
            log.info("요청 타입: {}", request.getType());
            log.info("요청 값: '{}'", request.getValue());
            log.info("요청 값 길이: {}", request.getValue().length());
            log.info("요청 값 공백 제거 후: '{}'", request.getValue().trim());
            
            DuplicateCheckDto.CheckResponse response = duplicateCheckService.checkDuplicate(request);
            
            log.info("응답 메시지: {}", response.getMessage());
            log.info("응답 isDuplicate: {}", response.isDuplicate());
            log.info("응답 available: {}", response.isAvailable());
            log.info("=== 중복확인 요청 완료 ===");
            
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", response.getMessage());
            successResponse.put("available", response.isAvailable());
            successResponse.put("duplicate", response.isDuplicate());
            successResponse.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            log.error("중복확인 오류: ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "중복확인 중 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<DuplicateCheckDto.CheckResponse> checkEmail(@PathVariable String email) {
        try {
            log.info("이메일 중복확인 요청: {}", email);
            DuplicateCheckDto.CheckResponse response = duplicateCheckService.checkEmail(email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("이메일 중복확인 오류: ", e);
            return ResponseEntity.badRequest().body(
                new DuplicateCheckDto.CheckResponse("이메일 중복확인 중 오류가 발생했습니다: " + e.getMessage(), false)
            );
        }
    }

    @GetMapping("/check-nickname/{nickname}")
    public ResponseEntity<DuplicateCheckDto.CheckResponse> checkNickname(@PathVariable String nickname) {
        try {
            log.info("닉네임 중복확인 요청: {}", nickname);
            DuplicateCheckDto.CheckResponse response = duplicateCheckService.checkNickname(nickname);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("닉네임 중복확인 오류: ", e);
            return ResponseEntity.badRequest().body(
                new DuplicateCheckDto.CheckResponse("닉네임 중복확인 중 오류가 발생했습니다: " + e.getMessage(), false)
            );
        }
    }
} 