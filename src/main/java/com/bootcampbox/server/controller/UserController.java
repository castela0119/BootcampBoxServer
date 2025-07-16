package com.bootcampbox.server.controller;

import com.bootcampbox.server.dto.UserDto;
import com.bootcampbox.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@Valid @RequestBody UserDto.SignUpRequest request) {
        log.info("회원가입 요청: {}", request.getEmail());
            UserDto.SignUpResponse response = userService.signUp(request);
        
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("success", true);
        successResponse.put("message", "회원가입이 완료되었습니다.");
        successResponse.put("data", response);
        successResponse.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(successResponse);
    }

    // === 관리자 계정 생성 (개발용) ===
    @PostMapping("/admin/create")
    public ResponseEntity<UserDto.SimpleResponse> createAdminUser(@Valid @RequestBody UserDto.AdminSignUpRequest request) {
        try {
            log.info("관리자 계정 생성 요청: {} (등급: {})", request.getEmail(), request.getRoleType());
            UserDto.SimpleResponse response = userService.createAdminUser(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("관리자 계정 생성 오류: ", e);
            return ResponseEntity.badRequest().body(new UserDto.SimpleResponse("관리자 계정 생성 실패: " + e.getMessage(), false));
        }
    }

    private void saveErrorLogToFile(Exception e, UserDto.SignUpRequest request) {
        try {
            String logDirPath = "logs/error";
            File logDir = new File(logDirPath);
            if (!logDir.exists()) logDir.mkdirs();
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String logFileName = String.format("error-signup-%s.log", date);
            File logFile = new File(logDir, logFileName);
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write("[회원가입 에러] " + LocalDateTime.now() + "\n");
                writer.write("요청 데이터: " + request.toString() + "\n");
                writer.write("에러 메시지: " + e.toString() + "\n");
                for (StackTraceElement ste : e.getStackTrace()) {
                    writer.write("    at " + ste.toString() + "\n");
                }
                writer.write("-----------------------------\n");
            }
        } catch (IOException ioException) {
            log.error("에러 로그 파일 저장 실패", ioException);
        }
    }
} 