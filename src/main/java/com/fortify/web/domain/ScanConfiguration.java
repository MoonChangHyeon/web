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
public class ScanConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Build ID는 필수입니다.")
    private String buildId;

    @NotBlank(message = "Classpath는 필수입니다.")
    private String classPath;

    @NotBlank(message = "분석할 파일 경로는 필수입니다.")
    private String filesToScan;
}
