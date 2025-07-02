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
public class FortifySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Fortify SCA 설치 경로는 필수입니다.")
    private String fortifyScaPath;

    @NotBlank(message = "Fortify Tools 설치 경로는 필수입니다.")
    private String fortifyToolsPath;

    @NotBlank(message = "리포트 출력 디렉토리는 필수입니다.")
    private String reportOutputDirectory;

    // 파생 경로 (DB에 저장하지 않고 transient로 처리하거나, 필요시 저장)
    // 여기서는 편의상 저장 필드로 추가
    private String sourceanalyzerExecutable; // bin/sourceanalyzer
    private String scaExternalMetadataDir; // Core/config/ExternalMetadata
    private String scaPropertiesDir; // Core/config

    private String reportGeneratorExecutable; // bin/ReportGenerator
    private String reportTemplatesDir; // Core/config/reports

    // Report Configuration Default Settings
    private String defaultReportTemplate;
    private String defaultReportUser;
    private boolean defaultReportShowRemoved;
    private boolean defaultReportShowSuppressed;
    private boolean defaultReportShowHidden;
    private String defaultReportFilterSet;

    // Scan Settings
    private String buildMemory;
    private Long maxAnalysisFileSize;

    private String fortifyUploadsDirectory;
}
