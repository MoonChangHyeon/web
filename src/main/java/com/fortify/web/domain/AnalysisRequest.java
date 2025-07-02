package com.fortify.web.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AnalysisRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String buildId;

    private String fileName;

    private long fileSize;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;

    private String errorMessage;

    private LocalDateTime requestTime;

    private LocalDateTime lastUpdateTime;

    @PrePersist
    protected void onCreate() {
        requestTime = LocalDateTime.now();
        lastUpdateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdateTime = LocalDateTime.now();
    }
}
