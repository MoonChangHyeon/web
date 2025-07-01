package com.fortify.web.service;

import com.fortify.web.domain.FortifySetting;
import com.fortify.web.domain.ReportConfiguration;
import com.fortify.web.repository.ReportConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ReportConfigurationServiceImpl implements ReportConfigurationService {

    private final ReportConfigurationRepository reportConfigurationRepository;
    private final FortifySettingService fortifySettingService;

    public ReportConfigurationServiceImpl(ReportConfigurationRepository reportConfigurationRepository, FortifySettingService fortifySettingService) {
        this.reportConfigurationRepository = reportConfigurationRepository;
        this.fortifySettingService = fortifySettingService;
    }

    @Override
    public List<ReportConfiguration> findAll() {
        return reportConfigurationRepository.findAll();
    }

    @Override
    public ReportConfiguration findById(Long id) {
        return reportConfigurationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid report configuration Id:" + id));
    }

    @Override
    public ReportConfiguration save(ReportConfiguration reportConfiguration) {
        return reportConfigurationRepository.save(reportConfiguration);
    }

    @Override
    public void deleteById(Long id) {
        reportConfigurationRepository.deleteById(id);
    }

    @Override
    public String generateReport(Long id) {
        ReportConfiguration config = findById(id);
        FortifySetting fortifySetting = fortifySettingService.getFortifySetting();

        if (fortifySetting == null || fortifySetting.getReportGeneratorExecutable() == null || fortifySetting.getReportGeneratorExecutable().isEmpty()) {
            return "ReportGenerator 실행 경로가 설정되지 않았습니다. Fortify Settings에서 설정해주세요.";
        }

        // 리포트 출력 디렉토리 구조 생성
        String baseOutputDirectory = fortifySetting.getReportOutputDirectory();
        String fortifyOutputBase = baseOutputDirectory + File.separator + "Fortify";
        String reportTypeDir = fortifyOutputBase + File.separator + config.getFormat().toUpperCase();

        File outputDirFile = new File(reportTypeDir);
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs();
        }

        List<String> command = new ArrayList<>();
        command.add(fortifySetting.getReportGeneratorExecutable());
        command.add("-format");
        command.add(config.getFormat());
        command.add("-f");
        // 리포트 출력 디렉토리와 파일명을 결합
        String fullOutputFilePath = reportTypeDir + File.separator + config.getOutputFilename();
        command.add(fullOutputFilePath);
        command.add("-source");
        command.add(config.getSourceFprPath());

        String template = (config.getTemplate() != null && !config.getTemplate().isEmpty()) ? config.getTemplate() : fortifySetting.getDefaultReportTemplate();
        String user = (config.getUser() != null && !config.getUser().isEmpty()) ? config.getUser() : fortifySetting.getDefaultReportUser();
        boolean showRemoved = config.isShowRemoved() || fortifySetting.isDefaultReportShowRemoved();
        boolean showSuppressed = config.isShowSuppressed() || fortifySetting.isDefaultReportShowSuppressed();
        boolean showHidden = config.isShowHidden() || fortifySetting.isDefaultReportShowHidden();
        String filterSet = (config.getFilterSet() != null && !config.getFilterSet().isEmpty()) ? config.getFilterSet() : fortifySetting.getDefaultReportFilterSet();

        if (template != null && !template.isEmpty()) {
            command.add("-template");
            command.add(template);
        }
        if (user != null && !user.isEmpty()) {
            command.add("-user");
            command.add(user);
        }
        if (showRemoved) {
            command.add("-showRemoved");
        }
        if (showSuppressed) {
            command.add("-showSuppressed");
        }
        if (showHidden) {
            command.add("-showHidden");
        }
        if (filterSet != null && !filterSet.isEmpty()) {
            command.add("-filterSet");
            command.add(filterSet);
        }

        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new java.io.File("web")); // 'web' 디렉토리에서 실행
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            output.append("\nExited with code: ").append(exitCode);

        } catch (IOException | InterruptedException e) {
            output.append("\nError generating report: ").append(e.getMessage());
            Thread.currentThread().interrupt();
        }
        return output.toString();
    }
}
