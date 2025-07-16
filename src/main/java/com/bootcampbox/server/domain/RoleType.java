package com.bootcampbox.server.domain;

public enum RoleType {
    SUPER_ADMIN("최고관리자", "모든 권한을 가진 최고 관리자"),
    ADMIN("일반관리자", "일반적인 관리자 권한"),
    MANAGER("매니저", "게시글/댓글 관리 및 운영 권한"),
    SUPPORT("고객지원", "고객 지원 및 문의 처리 권한");

    private final String description;
    private final String detail;

    RoleType(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }

    public String getDescription() {
        return description;
    }

    public String getDetail() {
        return detail;
    }
} 