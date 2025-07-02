package com.fortify.web.domain;

public enum AnalysisStatus {
    PENDING("요청 대기 중"),
    QUEUED("분석 대기열"),
    BUILDING("빌드 진행 중"),
    ANALYZING("분석 진행 중"),
    REPORTING("레포트 생성 중"),
    COMPLETED("요청 완료"),
    FAILED("요청 실패"),
    CANCELLED("요청 취소됨");

    private final String description;

    AnalysisStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
