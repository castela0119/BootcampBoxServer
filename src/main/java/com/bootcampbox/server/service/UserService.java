package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.User;
import com.bootcampbox.server.domain.Post;
import com.bootcampbox.server.domain.PostLike;
import com.bootcampbox.server.domain.Bookmark;
import com.bootcampbox.server.dto.UserDto;
import com.bootcampbox.server.dto.MyPageDto;
import com.bootcampbox.server.repository.UserRepository;
import com.bootcampbox.server.repository.PostRepository;
import com.bootcampbox.server.repository.PostLikeRepository;
import com.bootcampbox.server.repository.BookmarkRepository;
import com.bootcampbox.server.repository.CommentRepository; // 추가
import com.bootcampbox.server.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository; // 추가
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    public UserDto.SignUpResponse signUp(UserDto.SignUpRequest request) {
        // 이메일 인증 여부 체크
        if (!emailVerificationService.isEmailVerified(request.getEmail())) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다. 인증 후 가입해주세요.");
        }
        // 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        
        // username 설정 (전화번호가 유효하면 전화번호, 아니면 이메일 사용)
        String username;
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            username = request.getPhoneNumber().trim();
        } else {
            username = request.getEmail();
        }
        
        // username 중복 검사
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
        }
        
        // 전화번호가 제공된 경우에만 중복 검사
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new IllegalArgumentException("이미 가입된 휴대폰 번호입니다.");
            }
        }
        
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(username); // 위에서 정의된 username 사용
        
        // phoneNumber 설정 (빈 문자열이면 null로 저장)
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            user.setPhoneNumber(request.getPhoneNumber().trim());
        } else {
            user.setPhoneNumber(null);
        }
        user.setNickname(request.getNickname());
        user.setPassword(encodedPassword);
        user.setUserType(request.getUserType());
        user.setZipcode(request.getZipcode());
        user.setAddress(request.getAddress());
        user.setAddressDetail(request.getAddressDetail());
        // 날짜 변환
        if (request.getEnlistDate() != null && !request.getEnlistDate().isEmpty()) {
            try {
                user.setEnlistDate(LocalDate.parse(request.getEnlistDate()));
            } catch (Exception e) {
                // 날짜 파싱 실패 시 무시
            }
        }
        if (request.getDischargeDate() != null && !request.getDischargeDate().isEmpty()) {
            try {
                user.setDischargeDate(LocalDate.parse(request.getDischargeDate()));
            } catch (Exception e) {
                // 날짜 파싱 실패 시 무시
            }
        }
        if (request.getBoyfriendEnlistDate() != null && !request.getBoyfriendEnlistDate().isEmpty()) {
            try {
                user.setBoyfriendEnlistDate(LocalDate.parse(request.getBoyfriendEnlistDate()));
            } catch (Exception e) {
                // 날짜 파싱 실패 시 무시
            }
        }
        if (request.getBoyfriendDischargeDate() != null && !request.getBoyfriendDischargeDate().isEmpty()) {
            try {
                user.setBoyfriendDischargeDate(LocalDate.parse(request.getBoyfriendDischargeDate()));
            } catch (Exception e) {
                // 날짜 파싱 실패 시 무시
            }
        }

        User savedUser = userRepository.save(user);
        return UserDto.SignUpResponse.from(savedUser);
    }

    public MyPageDto.UserInfoResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        MyPageDto.UserInfoResponse response = MyPageDto.UserInfoResponse.from(user);
        
        // 🆕 활동 통계 데이터 추가
        long totalPosts = postRepository.countByUser(user);
        long totalComments = commentRepository.countByUser(user);
        long receivedLikesCount = postRepository.sumLikeCountByUser(user);
        long totalBookmarks = bookmarkRepository.countByUser(user);
        
        response.setPostCount((int) totalPosts);
        response.setCommentCount((int) totalComments);
        response.setTotal_received_likes((int) receivedLikesCount); // receivedLikesCount → total_received_likes로 변경
        response.setBookmarkCount((int) totalBookmarks);
        
        // 마지막 활동 시간 계산
        LocalDateTime lastActivityAt = null;
        
        // 마지막 게시글 시간
        LocalDateTime lastPostAt = null;
        try {
            lastPostAt = postRepository.findTopByUserOrderByCreatedAtDesc(user)
                    .map(Post::getCreatedAt)
                    .orElse(null);
        } catch (Exception e) {
            // 오류 발생 시 무시하고 계속 진행
        }
        
        // 마지막 좋아요 시간
        LocalDateTime lastLikeAt = null;
        try {
            lastLikeAt = postLikeRepository.findTopByUserOrderByCreatedAtDesc(user)
                    .map(PostLike::getCreatedAt)
                    .orElse(null);
        } catch (Exception e) {
            // 오류 발생 시 무시하고 계속 진행
        }
        
        // 마지막 북마크 시간
        LocalDateTime lastBookmarkAt = null;
        try {
            lastBookmarkAt = bookmarkRepository.findTopByUserOrderByCreatedAtDesc(user)
                    .map(Bookmark::getCreatedAt)
                    .orElse(null);
        } catch (Exception e) {
            // 오류 발생 시 무시하고 계속 진행
        }
        
        // 가장 최근 활동 시간 찾기
        if (lastPostAt != null && (lastActivityAt == null || lastPostAt.isAfter(lastActivityAt))) {
            lastActivityAt = lastPostAt;
        }
        if (lastLikeAt != null && (lastActivityAt == null || lastLikeAt.isAfter(lastActivityAt))) {
            lastActivityAt = lastLikeAt;
        }
        if (lastBookmarkAt != null && (lastActivityAt == null || lastBookmarkAt.isAfter(lastActivityAt))) {
            lastActivityAt = lastBookmarkAt;
        }
        
        response.setLastActivityAt(lastActivityAt);
        
        return response;
    }

    public MyPageDto.UpdateUserResponse updateUserInfo(String email, MyPageDto.UpdateUserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 닉네임 중복 검사 (다른 사용자가 사용 중인지 확인)
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
            user.setNickname(request.getNickname());
        }

        // 기본 정보 업데이트
        if (request.getZipcode() != null) user.setZipcode(request.getZipcode());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getAddressDetail() != null) user.setAddressDetail(request.getAddressDetail());

        // 날짜 정보 업데이트
        if (request.getEnlistDate() != null) {
            user.setEnlistDate(request.getEnlistDate());
        }
        if (request.getDischargeDate() != null) {
            user.setDischargeDate(request.getDischargeDate());
        }

        // 곰신 정보 업데이트
        if (request.getBoyfriendEnlistDate() != null) {
            user.setBoyfriendEnlistDate(request.getBoyfriendEnlistDate());
        }
        if (request.getBoyfriendDischargeDate() != null) {
            user.setBoyfriendDischargeDate(request.getBoyfriendDischargeDate());
        }

        // updatedAt 업데이트
        user.setUpdatedAt(java.time.LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        MyPageDto.UserInfoResponse userInfo = MyPageDto.UserInfoResponse.from(updatedUser);

        return new MyPageDto.UpdateUserResponse(
            "사용자 정보가 성공적으로 업데이트되었습니다.",
            true,
            userInfo
        );
    }

    // 내가 쓴 글 목록 조회
    public Page<MyPageDto.MyPostResponse> getMyPosts(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Page<Post> posts = postRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return posts.map(MyPageDto.MyPostResponse::from);
    }

    // 내가 좋아요한 글 목록 조회
    public Page<MyPageDto.MyLikedPostResponse> getMyLikedPosts(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Page<PostLike> postLikes = postLikeRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return postLikes.map(postLike -> 
            MyPageDto.MyLikedPostResponse.from(postLike.getPost(), postLike.getCreatedAt())
        );
    }

    // 내가 북마크한 글 목록 조회
    public Page<MyPageDto.MyBookmarkedPostResponse> getMyBookmarkedPosts(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Page<Bookmark> bookmarks = bookmarkRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return bookmarks.map(bookmark -> 
            MyPageDto.MyBookmarkedPostResponse.from(bookmark.getPost(), bookmark.getCreatedAt())
        );
    }

    // 내 활동 요약 조회
    public MyPageDto.MyActivitySummaryResponse getMyActivitySummary(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        long totalPosts = postRepository.countByUser(user);
        long totalComments = commentRepository.countByUser(user);
        long totalLikes = postLikeRepository.countByUser(user);
        long totalBookmarks = bookmarkRepository.countByUser(user);
        
        // 마지막 활동 시간 계산 (간단한 버전)
        LocalDateTime lastActivityAt = postRepository.findTopByUserOrderByCreatedAtDesc(user)
                .map(Post::getCreatedAt)
                .orElse(null);
        
        return new MyPageDto.MyActivitySummaryResponse(
                (int) totalPosts,
                (int) totalComments,
                (int) totalLikes,
                (int) totalBookmarks,
                lastActivityAt
        );
    }

    // User ID로 User 엔티티 조회 (내부용)
    public User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
    }
} 