package com.bootcampbox.server.service;

import com.bootcampbox.server.domain.*;
import com.bootcampbox.server.dto.AdminDto;
import com.bootcampbox.server.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final PostReportRepository postReportRepository;
    private final CommentReportRepository commentReportRepository;

    // === 사용자 관리 ===
    public AdminDto.UserListResponse getAllUsers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<User> userPage;
        if (search != null && !search.trim().isEmpty()) {
            userPage = userRepository.findByUsernameContainingOrNicknameContaining(search, search, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return AdminDto.UserListResponse.from(
            userPage.getContent(),
            userPage.getTotalElements(),
            page,
            userPage.getTotalPages(),
            userPage.hasNext(),
            userPage.hasPrevious()
        );
    }

    @Transactional
    public AdminDto.SimpleResponse updateUser(Long userId, AdminDto.UpdateUserRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return new AdminDto.SimpleResponse("사용자를 찾을 수 없습니다.", false);
        }

        User user = userOpt.get();
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getUserType() != null) {
            user.setUserType(request.getUserType());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        user.setActive(request.isActive());

        userRepository.save(user);
        return new AdminDto.SimpleResponse("사용자 정보가 업데이트되었습니다.", true);
    }

    @Transactional
    public AdminDto.SimpleResponse deleteUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return new AdminDto.SimpleResponse("사용자를 찾을 수 없습니다.", false);
        }

        userRepository.delete(userOpt.get());
        return new AdminDto.SimpleResponse("사용자가 삭제되었습니다.", true);
    }

    // === 게시글 관리 ===
    public AdminDto.PostListResponse getAllPosts(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Post> postPage;
        if (search != null && !search.trim().isEmpty()) {
            postPage = postRepository.findByTitleContainingOrContentContaining(search, search, pageable);
        } else {
            postPage = postRepository.findAll(pageable);
        }

        return AdminDto.PostListResponse.from(
            postPage.getContent(),
            postPage.getTotalElements(),
            page,
            postPage.getTotalPages(),
            postPage.hasNext(),
            postPage.hasPrevious()
        );
    }

    @Transactional
    public AdminDto.SimpleResponse deletePost(Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return new AdminDto.SimpleResponse("게시글을 찾을 수 없습니다.", false);
        }

        postRepository.delete(postOpt.get());
        return new AdminDto.SimpleResponse("게시글이 삭제되었습니다.", true);
    }

    // === 댓글 관리 ===
    public AdminDto.CommentListResponse getAllComments(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Comment> commentPage;
        if (search != null && !search.trim().isEmpty()) {
            commentPage = commentRepository.findByContentContaining(search, pageable);
        } else {
            commentPage = commentRepository.findAll(pageable);
        }

        return AdminDto.CommentListResponse.from(
            commentPage.getContent(),
            commentPage.getTotalElements(),
            page,
            commentPage.getTotalPages(),
            commentPage.hasNext(),
            commentPage.hasPrevious()
        );
    }

    public AdminDto.CommentListResponse getReportedComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findByReportCountGreaterThan(0, pageable);

        return AdminDto.CommentListResponse.from(
            commentPage.getContent(),
            commentPage.getTotalElements(),
            page,
            commentPage.getTotalPages(),
            commentPage.hasNext(),
            commentPage.hasPrevious()
        );
    }

    @Transactional
    public AdminDto.SimpleResponse deleteComment(Long commentId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            return new AdminDto.SimpleResponse("댓글을 찾을 수 없습니다.", false);
        }

        commentRepository.delete(commentOpt.get());
        return new AdminDto.SimpleResponse("댓글이 삭제되었습니다.", true);
    }

    // === 상품 관리 ===
    @Transactional
    public AdminDto.SimpleResponse createProduct(AdminDto.CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setExternalUrl(request.getExternalUrl());
        product.setDescription(request.getDescription());
        product.setActive(true);

        productRepository.save(product);
        return new AdminDto.SimpleResponse("상품이 등록되었습니다.", true);
    }

    @Transactional
    public AdminDto.SimpleResponse updateProduct(Long productId, AdminDto.UpdateProductRequest request) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return new AdminDto.SimpleResponse("상품을 찾을 수 없습니다.", false);
        }

        Product product = productOpt.get();
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getExternalUrl() != null) {
            product.setExternalUrl(request.getExternalUrl());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        product.setActive(request.isActive());

        productRepository.save(product);
        return new AdminDto.SimpleResponse("상품 정보가 업데이트되었습니다.", true);
    }

    @Transactional
    public AdminDto.SimpleResponse deleteProduct(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return new AdminDto.SimpleResponse("상품을 찾을 수 없습니다.", false);
        }

        productRepository.delete(productOpt.get());
        return new AdminDto.SimpleResponse("상품이 삭제되었습니다.", true);
    }

    // === 통계 대시보드 ===
    public AdminDto.DashboardStats getDashboardStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);

        long totalUsers = userRepository.count();
        long totalPosts = postRepository.count();
        long totalComments = commentRepository.count();
        long totalProducts = productRepository.count();
        
        long todayNewUsers = userRepository.countByCreatedAtBetween(todayStart, todayEnd);
        long todayNewPosts = postRepository.countByCreatedAtBetween(todayStart, todayEnd);
        long todayNewComments = commentRepository.countByCreatedAtBetween(todayStart, todayEnd);
        long reportedComments = commentRepository.countByReportCountGreaterThan(0);

        return new AdminDto.DashboardStats(
            totalUsers, totalPosts, totalComments, totalProducts,
            todayNewUsers, todayNewPosts, todayNewComments, reportedComments
        );
    }

    // === 신고 관리 ===
    public AdminDto.PostReportListResponse getPostReports(int page, int size, String status, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<PostReport> reportPage;
        if (status != null && !status.trim().isEmpty()) {
            try {
                ReportStatus reportStatus = ReportStatus.valueOf(status.toUpperCase());
                reportPage = postReportRepository.findByStatusOrderByCreatedAtDesc(reportStatus, pageable);
            } catch (IllegalArgumentException e) {
                reportPage = postReportRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        } else if (type != null && !type.trim().isEmpty()) {
            try {
                ReportType reportType = ReportType.valueOf(type.toUpperCase());
                reportPage = postReportRepository.findByReportTypeOrderByCreatedAtDesc(reportType, pageable);
            } catch (IllegalArgumentException e) {
                reportPage = postReportRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        } else {
            reportPage = postReportRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return AdminDto.PostReportListResponse.from(
            reportPage.getContent(),
            reportPage.getTotalElements(),
            page,
            reportPage.getTotalPages(),
            reportPage.hasNext(),
            reportPage.hasPrevious()
        );
    }

    public AdminDto.CommentReportListResponse getCommentReports(int page, int size, String status, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<CommentReport> reportPage;
        if (status != null && !status.trim().isEmpty()) {
            try {
                ReportStatus reportStatus = ReportStatus.valueOf(status.toUpperCase());
                reportPage = commentReportRepository.findByStatusOrderByCreatedAtDesc(reportStatus, pageable);
            } catch (IllegalArgumentException e) {
                reportPage = commentReportRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        } else if (type != null && !type.trim().isEmpty()) {
            try {
                ReportType reportType = ReportType.valueOf(type.toUpperCase());
                reportPage = commentReportRepository.findByReportTypeOrderByCreatedAtDesc(reportType, pageable);
            } catch (IllegalArgumentException e) {
                reportPage = commentReportRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        } else {
            reportPage = commentReportRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return AdminDto.CommentReportListResponse.from(
            reportPage.getContent(),
            reportPage.getTotalElements(),
            page,
            reportPage.getTotalPages(),
            reportPage.hasNext(),
            reportPage.hasPrevious()
        );
    }
} 