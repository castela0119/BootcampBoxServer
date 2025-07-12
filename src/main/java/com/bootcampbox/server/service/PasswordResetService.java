package com.bootcampbox.server.service;

import com.bootcampbox.server.dto.PasswordResetDto;
import com.bootcampbox.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PasswordResetDto.ResetPasswordResponse resetPassword(PasswordResetDto.ResetPasswordRequest request) {
        return userRepository.findByPhoneNumber(request.getPhoneNumber())
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    userRepository.save(user);
                    return new PasswordResetDto.ResetPasswordResponse("비밀번호가 성공적으로 변경되었습니다.", true);
                })
                .orElseGet(() -> new PasswordResetDto.ResetPasswordResponse("해당 휴대폰 번호로 가입된 사용자가 없습니다.", false));
    }
} 