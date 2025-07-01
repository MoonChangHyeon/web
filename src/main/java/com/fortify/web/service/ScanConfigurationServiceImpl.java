package com.fortify.web.service;

import com.fortify.web.domain.ScanConfiguration;
import com.fortify.web.domain.FortifySetting;
import com.fortify.web.repository.ScanConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
@Transactional
public class ScanConfigurationServiceImpl implements ScanConfigurationService {

    private final ScanConfigurationRepository scanConfigurationRepository;
    private final FortifySettingService fortifySettingService;

    public ScanConfigurationServiceImpl(ScanConfigurationRepository scanConfigurationRepository, FortifySettingService fortifySettingService) {
        this.scanConfigurationRepository = scanConfigurationRepository;
        this.fortifySettingService = fortifySettingService;
    }

    @Override
    public List<ScanConfiguration> findAll() {
        return scanConfigurationRepository.findAll();
    }

    @Override
    public ScanConfiguration findById(Long id) {
        return scanConfigurationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid scan configuration Id:" + id));
    }

    @Override
    public ScanConfiguration save(ScanConfiguration scanConfiguration) {
        return scanConfigurationRepository.save(scanConfiguration);
    }

    @Override
    public void deleteById(Long id) {
        scanConfigurationRepository.deleteById(id);
    }

    @Override
    public String runScan(Long id) {
        ScanConfiguration config = findById(id);
        FortifySetting fortifySetting = fortifySettingService.getFortifySetting();
        if (fortifySetting == null || fortifySetting.getFortifyScaPath() == null || fortifySetting.getFortifyScaPath().isEmpty()) {
            return "Fortify SCA 설치 경로가 설정되지 않았습니다. Fortify Settings에서 설정해주세요.";
        }

        String sourceAnalyzerExecutable = fortifySetting.getSourceanalyzerExecutable();
        String buildId = config.getBuildId();
        StringBuilder output = new StringBuilder();

        try {
            // 1. Translate 단계
            output.append("--- Fortify Translate 시작 ---\n");
            ProcessBuilder translatePb = new ProcessBuilder(
                sourceAnalyzerExecutable,
                "-b", buildId,
                "-cp", config.getClassPath(),
                config.getFilesToScan()
            );
            translatePb.directory(new java.io.File(".")); // 프로젝트 루트에서 실행
            translatePb.redirectErrorStream(true);
            int translateExitCode = executeProcess(translatePb, output);

            if (translateExitCode != 0) {
                output.append("\nFortify Translate 실패. 종료 코드: ").append(translateExitCode);
                return output.toString();
            }
            output.append("\n--- Fortify Translate 성공 ---\n\n");

            // 2. Scan 단계
            output.append("--- Fortify Scan 시작 ---\n");

            // FPR 출력 디렉토리 구조 생성
            String baseOutputDirectory = fortifySetting.getReportOutputDirectory();
            String fortifyOutputBase = baseOutputDirectory + File.separator + "Fortify";
            String fprOutputDirectory = fortifyOutputBase + File.separator + "FPR";

            File outputDirFile = new File(fprOutputDirectory);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();
            }

            String fprPath = fprOutputDirectory + File.separator + buildId + ".fpr";
            ProcessBuilder scanPb = new ProcessBuilder(
                sourceAnalyzerExecutable,
                "-b", buildId,
                "-scan",
                "-f", fprPath
            );
            scanPb.directory(new java.io.File(".")); // 프로젝트 루트에서 실행
            scanPb.redirectErrorStream(true);
            int scanExitCode = executeProcess(scanPb, output);

            if (scanExitCode == 0) {
                output.append("\n--- Fortify Scan 성공 ---\n");
                output.append("리포트 생성 위치: ").append(fprPath);

                // 3. Report Generation 단계 (PDF)
                output.append("\n--- Fortify Report (PDF) 생성 시작 ---\n");
                String reportGeneratorExecutable = fortifySetting.getReportGeneratorExecutable();
                String reportOutputBase = fortifySetting.getReportOutputDirectory() + File.separator + "Fortify";
                String pdfOutputDirectory = reportOutputBase + File.separator + "PDF";
                String xmlOutputDirectory = reportOutputBase + File.separator + "XML";

                File pdfOutputDirFile = new File(pdfOutputDirectory);
                if (!pdfOutputDirFile.exists()) {
                    pdfOutputDirFile.mkdirs();
                }
                File xmlOutputDirFile = new File(xmlOutputDirectory);
                if (!xmlOutputDirFile.exists()) {
                    xmlOutputDirFile.mkdirs();
                }

                String pdfReportPath = pdfOutputDirectory + File.separator + buildId + ".pdf";
                ProcessBuilder pdfReportPb = new ProcessBuilder(
                    reportGeneratorExecutable,
                    "-format", "pdf",
                    "-f", pdfReportPath,
                    "-source", fprPath
                );
                pdfReportPb.directory(new java.io.File("."));
                pdfReportPb.redirectErrorStream(true);
                int pdfReportExitCode = executeProcess(pdfReportPb, output);

                if (pdfReportExitCode == 0) {
                    output.append("\n--- Fortify Report (PDF) 생성 성공 ---\n");
                    output.append("PDF 리포트 위치: ").append(pdfReportPath);
                } else {
                    output.append("\nFortify Report (PDF) 생성 실패. 종료 코드: ").append(pdfReportExitCode);
                }

                // 4. Report Generation 단계 (XML)
                output.append("\n--- Fortify Report (XML) 생성 시작 ---\n");
                String xmlReportPath = xmlOutputDirectory + File.separator + buildId + ".xml";
                ProcessBuilder xmlReportPb = new ProcessBuilder(
                    reportGeneratorExecutable,
                    "-format", "xml",
                    "-f", xmlReportPath,
                    "-source", fprPath
                );
                xmlReportPb.directory(new java.io.File("."));
                xmlReportPb.redirectErrorStream(true);
                int xmlReportExitCode = executeProcess(xmlReportPb, output);

                if (xmlReportExitCode == 0) {
                    output.append("\n--- Fortify Report (XML) 생성 성공 ---\n");
                    output.append("XML 리포트 위치: ").append(xmlReportPath);
                } else {
                    output.append("\nFortify Report (XML) 생성 실패. 종료 코드: ").append(xmlReportExitCode);
                }

            } else {
                output.append("\nFortify Scan 실패. 종료 코드: ").append(scanExitCode);
            }

        } catch (IOException | InterruptedException e) {
            output.append("\n스캔 실행 중 오류 발생: ").append(e.getMessage());
            Thread.currentThread().interrupt();
        }
        return output.toString();
    }

    private int executeProcess(ProcessBuilder processBuilder, StringBuilder output) throws IOException, InterruptedException {
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return process.waitFor();
    }
}
