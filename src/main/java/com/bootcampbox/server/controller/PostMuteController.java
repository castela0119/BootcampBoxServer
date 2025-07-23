package com.bootcampbox.server.controller;

import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.dto.PostMuteDto;
import com.bootcampbox.server.repository.UserRepository;
import com.bootcampbox.server.service.PostMuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostMuteController {
    
    private final PostMuteService postMuteService;
    private final UserRepository userRepository;
    
    // 뮤트된 게시글 목록 조회 (더 구체적인 경로를 먼저 정의)
    @GetMapping("/muted")
    public ResponseEntity<PostMuteDto.ApiResponse> getMutedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // 인증되지 않은 사용자 처리
            if ("anonymousUser".equals(email)) {
                return ResponseEntity.status(401).body(
                    PostMuteDto.ApiResponse.error(
                        "인증이 필요합니다.",
                        Map.of("code", "UNAUTHORIZED", "details", "유효하지 않은 토큰입니다.")
                    )
                );
            }
            
            // email로 사용자 조회
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
            
            Long userId = user.getId();
            log.info("뮤트된 게시글 목록 조회 - 사용자: {} (ID: {}), 페이지: {}, 크기: {}", email, userId, page, size);
            
            PostMuteDto.MutedPostsResponse response = postMuteService.getUserMutedPosts(userId, page, size);
            
            return ResponseEntity.ok(PostMuteDto.ApiResponse.success(
                "뮤트된 게시글 목록을 성공적으로 조회했습니다.",
                response
            ));
        } catch (IllegalArgumentException e) {
            log.warn("뮤트된 게시글 목록 조회 실패 (사용자 없음): {}", e.getMessage());
            return ResponseEntity.status(401).body(
                PostMuteDto.ApiResponse.error(
                    "인증이 필요합니다.",
                    Map.of("code", "UNAUTHORIZED", "details", "사용자를 찾을 수 없습니다.")
                )
            );
        } catch (Exception e) {
            log.error("뮤트된 게시글 목록 조회 실패: ", e);
            return ResponseEntity.internalServerError().body(
                PostMuteDto.ApiResponse.error(
                    "뮤트된 게시글 목록 조회에 실패했습니다.",
                    Map.of("code", "INTERNAL_SERVER_ERROR", "details", e.getMessage())
                )
            );
        }
    }
    
    // 게시글 뮤트
    @PostMapping("/{postId}/mute")
    public ResponseEntity<PostMuteDto.ApiResponse> mutePost(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // 인증되지 않은 사용자 처리
            if ("anonymousUser".equals(email)) {
                return ResponseEntity.status(401).body(
                    PostMuteDto.ApiResponse.error(
                        "인증이 필요합니다.",
                        Map.of("code", "UNAUTHORIZED", "details", "유효하지 않은 토큰입니다.")
                    )
                );
            }
            
            // email로 사용자 조회
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
            
            Long userId = user.getId();
            log.info("게시글 뮤트 요청 - 사용자: {} (ID: {}), 게시글: {}", email, userId, postId);
            
            PostMuteDto.MuteResponse response = postMuteService.mutePost(userId, postId);
            
            return ResponseEntity.ok(PostMuteDto.ApiResponse.success(
                "게시글이 뮤트되었습니다.",
                response
            ));
        } catch (IllegalArgumentException e) {
            log.warn("게시글 뮤트 실패 (게시글 없음 또는 사용자 없음): {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("게시글 뮤트 실패 (이미 뮤트됨): {}", e.getMessage());
            return ResponseEntity.status(409).body(
                PostMuteDto.ApiResponse.error(
                    "이미 뮤트된 게시글입니다.",
                    Map.of("code", "ALREADY_MUTED", "details", e.getMessage())
                )
            );
        } catch (Exception e) {
            log.error("게시글 뮤트 실패: ", e);
            return ResponseEntity.internalServerError().body(
                PostMuteDto.ApiResponse.error(
                    "게시글 뮤트에 실패했습니다.",
                    Map.of("code", "INTERNAL_SERVER_ERROR", "details", e.getMessage())
                )
            );
        }
    }
    
    // 게시글 언뮤트
    @DeleteMapping("/{postId}/mute")
    public ResponseEntity<PostMuteDto.ApiResponse> unmutePost(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // 인증되지 않은 사용자 처리
            if ("anonymousUser".equals(email)) {
                return ResponseEntity.status(401).body(
                    PostMuteDto.ApiResponse.error(
                        "인증이 필요합니다.",
                        Map.of("code", "UNAUTHORIZED", "details", "유효하지 않은 토큰입니다.")
                    )
                );
            }
            
            // email로 사용자 조회
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
            
            Long userId = user.getId();
            log.info("게시글 언뮤트 요청 - 사용자: {} (ID: {}), 게시글: {}", email, userId, postId);
            
            PostMuteDto.UnmuteResponse response = postMuteService.unmutePost(userId, postId);
            
            return ResponseEntity.ok(PostMuteDto.ApiResponse.success(
                "게시글 뮤트가 해제되었습니다.",
                response
            ));
        } catch (IllegalArgumentException e) {
            log.warn("게시글 언뮤트 실패 (게시글 없음 또는 사용자 없음): {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("게시글 언뮤트 실패: ", e);
            return ResponseEntity.internalServerError().body(
                PostMuteDto.ApiResponse.error(
                    "게시글 언뮤트에 실패했습니다.",
                    Map.of("code", "INTERNAL_SERVER_ERROR", "details", e.getMessage())
                )
            );
        }
    }
    
    // 게시글 뮤트 상태 조회
    @GetMapping("/{postId}/mute")
    public ResponseEntity<PostMuteDto.ApiResponse> getMuteStatus(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // 인증되지 않은 사용자 처리
            if ("anonymousUser".equals(email)) {
                return ResponseEntity.status(401).body(
                    PostMuteDto.ApiResponse.error(
                        "인증이 필요합니다.",
                        Map.of("code", "UNAUTHORIZED", "details", "유효하지 않은 토큰입니다.")
                    )
                );
            }
            
            // email로 사용자 조회
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
            
            Long userId = user.getId();
            log.info("게시글 뮤트 상태 조회 - 사용자: {} (ID: {}), 게시글: {}", email, userId, postId);
            
            PostMuteDto.MuteStatusResponse response = postMuteService.getMuteStatus(userId, postId);
            
            return ResponseEntity.ok(PostMuteDto.ApiResponse.success(
                "뮤트 상태를 성공적으로 조회했습니다.",
                response
            ));
        } catch (IllegalArgumentException e) {
            log.warn("게시글 뮤트 상태 조회 실패 (게시글 없음 또는 사용자 없음): {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("게시글 뮤트 상태 조회 실패: ", e);
            return ResponseEntity.internalServerError().body(
                PostMuteDto.ApiResponse.error(
                    "뮤트 상태 조회에 실패했습니다.",
                    Map.of("code", "INTERNAL_SERVER_ERROR", "details", e.getMessage())
                )
            );
        }
    }
} 