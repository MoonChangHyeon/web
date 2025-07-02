package com.fortify.web.service;

import com.fortify.web.domain.FortifySetting;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AnalysisService {

    private final FortifySettingService fortifySettingService;

    public AnalysisService(FortifySettingService fortifySettingService) {
        this.fortifySettingService = fortifySettingService;
    }

    public void requestAnalysis(String buildId, MultipartFile file) throws Exception {
        FortifySetting setting = fortifySettingService.getFortifySetting();
        if (setting == null || setting.getMaxAnalysisFileSize() == null) {
            throw new Exception("분석 용량 제한이 설정되지 않았습니다.");
        }

        long maxFileSize = setting.getMaxAnalysisFileSize() * 1024 * 1024; // MB to Bytes
        if (file.getSize() > maxFileSize) {
            throw new Exception("파일 크기가 분석 가능 용량을 초과합니다.");
        }

        // TODO: 실제 분석 로직 추가 (예: 파일을 특정 위치에 저장하고, Fortify SCA 실행)
        System.out.println("Build ID: " + buildId);
        System.out.println("File Name: " + file.getOriginalFilename());
        System.out.println("File Size: " + file.getSize());

    }
}
