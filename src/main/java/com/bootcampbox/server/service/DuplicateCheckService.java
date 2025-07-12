package com.bootcampbox.server.service;

import com.bootcampbox.server.dto.DuplicateCheckDto;
import com.bootcampbox.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DuplicateCheckService {

    private final UserRepository userRepository;

    public DuplicateCheckDto.CheckResponse checkDuplicate(DuplicateCheckDto.CheckRequest request) {
        String value = request.getValue().trim();
        String type = request.getType();

        if (value.isEmpty()) {
            return new DuplicateCheckDto.CheckResponse("값을 입력해주세요.", false);
        }

        boolean isDuplicate = false;
        String message = "";

        switch (type) {
            case "email":
                isDuplicate = userRepository.existsByEmail(value);
                message = isDuplicate ? 
                    "이미 사용 중인 이메일입니다." : 
                    "사용 가능한 이메일입니다.";
                break;
                
            case "nickname":
                isDuplicate = userRepository.existsByNickname(value);
                message = isDuplicate ? 
                    "이미 사용 중인 닉네임입니다." : 
                    "사용 가능한 닉네임입니다.";
                break;
                
            default:
                return new DuplicateCheckDto.CheckResponse("잘못된 확인 타입입니다.", false);
        }

        return new DuplicateCheckDto.CheckResponse(message, isDuplicate);
    }

    // 개별 메서드로도 제공 (편의성)
    public DuplicateCheckDto.CheckResponse checkEmail(String email) {
        boolean isDuplicate = userRepository.existsByEmail(email);
        String message = isDuplicate ? 
            "이미 사용 중인 이메일입니다." : 
            "사용 가능한 이메일입니다.";
        return new DuplicateCheckDto.CheckResponse(message, isDuplicate);
    }

    public DuplicateCheckDto.CheckResponse checkNickname(String nickname) {
        boolean isDuplicate = userRepository.existsByNickname(nickname);
        String message = isDuplicate ? 
            "이미 사용 중인 닉네임입니다." : 
            "사용 가능한 닉네임입니다.";
        return new DuplicateCheckDto.CheckResponse(message, isDuplicate);
    }
} 