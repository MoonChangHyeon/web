package com.fortify.web.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String actor; // 활동을 수행한 사용자 (예: 유저명)

    @Column(nullable = false)
    private String action; // 수행된 작업 (예: CREATE_USER, UPDATE_USER)

    @Column(nullable = false)
    private String entityType; // 영향을 받은 엔티티의 타입 (예: User)

    @Column(nullable = false)
    private Long entityId; // 영향을 받은 엔티티의 ID

    @Column(columnDefinition = "TEXT")
    private String details; // 작업에 대한 추가 상세 정보 (JSON 또는 문자열)
}
