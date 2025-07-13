package com.bootcampbox.server.domain;

public enum ReportStatus {
    PENDING("대기중"),
    APPROVED("승인"),
    REJECTED("거부"),
    PROCESSING("처리중");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 