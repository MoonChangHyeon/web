package com.fortify.web.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ReportConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "리포트 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "출력 형식은 필수입니다.")
    private String format; // pdf, xml

    @NotBlank(message = "결과 파일명은 필수입니다.")
    private String outputFilename;

    @NotBlank(message = "FPR 소스 파일은 필수입니다.")
    private String sourceFprPath;

    private String template; // 템플릿 경로 (선택 사항)
    private String user; // 사용자명 (선택 사항)
    private boolean showRemoved; // 삭제된 이슈 포함 여부
    private boolean showSuppressed; // 무시된 이슈 포함 여부
    private boolean showHidden; // 숨겨진 이슈 포함 여부
    private String filterSet; // 필터셋 (선택 사항)
}
