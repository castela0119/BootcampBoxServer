package com.bootcampbox.server.domain;

public enum ReportType {
    COMMERCIAL_AD("상업적 광고"),
    ABUSE_DISCRIMINATION("욕설/비하"),
    PORNOGRAPHY_INAPPROPRIATE("음란물/불건전 대화"),
    LEAK_IMPERSONATION_FRAUD("유출/사칭/사기"),
    ILLEGAL_VIDEO_DISTRIBUTION("불법촬영물 유통"),
    INAPPROPRIATE_FOR_BOARD("게시판 성격에 부적절"),
    TROLLING_SPAM("낚시/놀람/도배"),
    ETC("기타");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 