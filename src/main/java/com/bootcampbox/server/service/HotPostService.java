package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.dto.HotPostDto;
import com.bootcampbox.server.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HotPostService {

    private final PostRepository postRepository;

    /**
     * HOT 게시글 목록 조회
     */
    public HotPostDto.HotPostListResponse getHotPosts(int page, int size, int period) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage;
        
        if (period > 0) {
            // 특정 기간 내 HOT 게시글 조회
            LocalDateTime startDate = LocalDateTime.now().minusDays(period);
            postPage = postRepository.findHotPostsByPeriodOrderByHotScoreDesc(startDate, pageable);
        } else {
            // 전체 HOT 게시글 조회
            postPage = postRepository.findHotPostsOrderByHotScoreDesc(pageable);
        }
        
        List<HotPostDto.HotPostResponse> hotPostResponses = postPage.getContent().stream()
                .map(HotPostDto.HotPostResponse::from)
                .collect(Collectors.toList());
        
        return new HotPostDto.HotPostListResponse(
                hotPostResponses,
                postPage.getTotalElements(),
                postPage.getNumber(),
                postPage.getTotalPages(),
                postPage.hasNext(),
                postPage.hasPrevious()
        );
    }

    /**
     * HOT 게시글 통계 조회
     */
    public HotPostDto.HotStatsResponse getHotStats() {
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        long totalHotPosts = postRepository.countHotPosts();
        long todayHotPosts = postRepository.countHotPostsByPeriod(todayStart);
        
        Double averageHotScore = postRepository.getAverageHotScore();
        Double todayAverageHotScore = postRepository.getAverageHotScoreByPeriod(todayStart);
        
        return new HotPostDto.HotStatsResponse(
                totalHotPosts,
                todayHotPosts,
                averageHotScore != null ? averageHotScore : 0.0,
                todayAverageHotScore != null ? todayAverageHotScore : 0.0,
                LocalDateTime.now()
        );
    }

    /**
     * 특정 게시글의 HOT 점수 수동 조정 (관리자용)
     */
    public HotPostDto.HotScoreAdjustResponse adjustHotScore(Long postId, int newHotScore) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        int oldHotScore = post.getHotScore();
        boolean oldIsHot = post.isHot();
        
        // HOT 점수 수동 설정
        post.setHotScore(newHotScore);
        post.setHotUpdatedAt(LocalDateTime.now());
        
        // HOT 게시글 선정 기준: 80점 이상
        boolean newIsHot = newHotScore >= 80;
        post.setHot(newIsHot);
        
        postRepository.save(post);
        
        String message = String.format("HOT 점수가 %d에서 %d로 조정되었습니다.", oldHotScore, newHotScore);
        if (oldIsHot != newIsHot) {
            message += newIsHot ? " HOT 게시글로 선정되었습니다." : " HOT 게시글에서 제외되었습니다.";
        }
        
        log.info("HOT 점수 수동 조정 - 게시글: {}, 이전 점수: {}, 새 점수: {}, HOT 상태: {}", 
                postId, oldHotScore, newHotScore, newIsHot);
        
        return new HotPostDto.HotScoreAdjustResponse(message, oldHotScore, newHotScore, newIsHot, true);
    }

    /**
     * 게시글의 HOT 점수 업데이트 (이벤트 기반)
     */
    public void updatePostHotScore(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        boolean oldIsHot = post.isHot();
        int oldHotScore = post.getHotScore();
        
        // HOT 점수 재계산
        post.updateHotScore();
        
        boolean newIsHot = post.isHot();
        int newHotScore = post.getHotScore();
        
        postRepository.save(post);
        
        // HOT 상태 변경 시 로그
        if (oldIsHot != newIsHot) {
            log.info("HOT 상태 변경 - 게시글: {}, 제목: {}, 이전 점수: {}, 새 점수: {}, HOT 상태: {} -> {}", 
                    postId, post.getTitle(), oldHotScore, newHotScore, oldIsHot, newIsHot);
        }
    }

    /**
     * 정기적인 HOT 점수 재계산 (6시간마다)
     */
    @Scheduled(cron = "0 0 */6 * * *") // 00:00, 06:00, 12:00, 18:00
    @Transactional
    public void recalculateHotScores() {
        log.info("=== HOT 점수 재계산 배치 작업 시작 ===");
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(7); // 최근 7일
        int page = 0;
        int size = 1000;
        int totalProcessed = 0;
        int hotStatusChanged = 0;
        
        try {
            while (true) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Post> postPage = postRepository.findRecentPostsForBatch(startDate, pageable);
                
                if (postPage.isEmpty()) {
                    break;
                }
                
                for (Post post : postPage.getContent()) {
                    boolean oldIsHot = post.isHot();
                    int oldHotScore = post.getHotScore();
                    
                    // HOT 점수 재계산
                    post.updateHotScore();
                    
                    if (oldIsHot != post.isHot()) {
                        hotStatusChanged++;
                    }
                    
                    totalProcessed++;
                }
                
                postRepository.saveAll(postPage.getContent());
                page++;
                
                log.info("배치 처리 진행 - 페이지: {}, 처리된 게시글: {}, HOT 상태 변경: {}", 
                        page, totalProcessed, hotStatusChanged);
            }
            
            log.info("=== HOT 점수 재계산 완료 - 총 처리: {}, HOT 상태 변경: {} ===", 
                    totalProcessed, hotStatusChanged);
                    
        } catch (Exception e) {
            log.error("HOT 점수 재계산 중 오류 발생: ", e);
        }
    }

    /**
     * 오래된 HOT 게시글 자동 제외 (매일 새벽 2시)
     */
    @Scheduled(cron = "0 0 2 * * *") // 매일 02:00
    @Transactional
    public void excludeOldHotPosts() {
        log.info("=== 오래된 HOT 게시글 제외 작업 시작 ===");
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7); // 7일 이상
        List<Post> oldHotPosts = postRepository.findOldHotPosts(cutoffDate);
        
        int excludedCount = 0;
        
        for (Post post : oldHotPosts) {
            post.setHot(false);
            post.setHotUpdatedAt(LocalDateTime.now());
            excludedCount++;
            
            log.info("오래된 HOT 게시글 제외 - 게시글: {}, 제목: {}, 작성일: {}", 
                    post.getId(), post.getTitle(), post.getCreatedAt());
        }
        
        if (!oldHotPosts.isEmpty()) {
            postRepository.saveAll(oldHotPosts);
        }
        
        log.info("=== 오래된 HOT 게시글 제외 완료 - 제외된 게시글: {} ===", excludedCount);
    }

    /**
     * 게시글 생성 시 초기 HOT 점수 설정
     */
    public void initializePostHotScore(Post post) {
        post.updateHotScore();
        postRepository.save(post);
        
        log.info("새 게시글 HOT 점수 초기화 - 게시글: {}, 제목: {}, HOT 점수: {}, HOT 상태: {}", 
                post.getId(), post.getTitle(), post.getHotScore(), post.isHot());
    }
} 