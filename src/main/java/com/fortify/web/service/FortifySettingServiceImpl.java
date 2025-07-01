package com.fortify.web.service;

import com.fortify.web.domain.FortifySetting;
import com.fortify.web.repository.FortifySettingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
@Transactional
public class FortifySettingServiceImpl implements FortifySettingService {

    private final FortifySettingRepository fortifySettingRepository;

    public FortifySettingServiceImpl(FortifySettingRepository fortifySettingRepository) {
        this.fortifySettingRepository = fortifySettingRepository;
    }

    @PostConstruct
    public void init() {
        // 애플리케이션 시작 시 Fortify 설정이 없으면 기본값 저장
        if (fortifySettingRepository.count() == 0) {
            FortifySetting setting = new FortifySetting();
            setting.setFortifyScaPath("/opt/fortify/Fortify_SCA_and_Apps_23.2.0"); // 예시 기본값
            setting.setFortifyToolsPath("/opt/fortify/Fortify_SCA_and_Apps_23.2.0"); // 예시 기본값
            setting.setReportOutputDirectory("./reports"); // 예시 기본값
            setting.setDefaultReportTemplate(""); // 기본 템플릿 없음
            setting.setDefaultReportUser(""); // 기본 사용자 없음
            setting.setDefaultReportShowRemoved(false);
            setting.setDefaultReportShowSuppressed(false);
            setting.setDefaultReportShowHidden(false);
            setting.setDefaultReportFilterSet(""); // 기본 필터셋 없음
            calculateAndSetDerivedPaths(setting);
            fortifySettingRepository.save(setting);
        }
    }

    @Override
    public FortifySetting getFortifySetting() {
        // 항상 하나의 설정만 존재한다고 가정하고 첫 번째 설정을 반환
        return fortifySettingRepository.findAll().stream().findFirst().orElse(null);
    }

    @Override
    public FortifySetting saveFortifySetting(FortifySetting fortifySetting) {
        // 기존 설정이 있다면 ID를 유지하여 업데이트
        if (fortifySettingRepository.count() > 0 && fortifySetting.getId() == null) {
            FortifySetting existingSetting = fortifySettingRepository.findAll().stream().findFirst().orElse(null);
            if (existingSetting != null) {
                fortifySetting.setId(existingSetting.getId());
            }
        }
        calculateAndSetDerivedPaths(fortifySetting);
        return fortifySettingRepository.save(fortifySetting);
    }

    @Override
    public List<String> getReportTemplateFiles() {
        FortifySetting setting = getFortifySetting();
        if (setting == null || setting.getReportTemplatesDir() == null || setting.getReportTemplatesDir().isEmpty()) {
            return List.of();
        }

        java.io.File templateDir = new java.io.File(setting.getReportTemplatesDir());
        if (!templateDir.exists() || !templateDir.isDirectory()) {
            return List.of();
        }

        List<String> templateFiles = new java.util.ArrayList<>();
        for (java.io.File file : templateDir.listFiles()) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {
                templateFiles.add(file.getName());
            }
        }
        return templateFiles;
    }

    private void calculateAndSetDerivedPaths(FortifySetting setting) {
        String scaPath = setting.getFortifyScaPath();
        String toolsPath = setting.getFortifyToolsPath();

        if (scaPath != null && !scaPath.isEmpty()) {
            setting.setSourceanalyzerExecutable(scaPath + "/bin/sourceanalyzer");
            setting.setScaExternalMetadataDir(scaPath + "/Core/config/ExternalMetadata");
            setting.setScaPropertiesDir(scaPath + "/Core/config");
        }

        if (toolsPath != null && !toolsPath.isEmpty()) {
            setting.setReportGeneratorExecutable(toolsPath + "/bin/ReportGenerator");
            setting.setReportTemplatesDir(toolsPath + "/Core/config/reports");
        }
    }
}
