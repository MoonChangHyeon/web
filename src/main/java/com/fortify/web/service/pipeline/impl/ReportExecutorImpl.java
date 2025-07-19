package com.fortify.web.service.pipeline.impl;

import com.fortify.web.domain.FortifySetting;
import com.fortify.web.service.FortifySettingService;
import com.fortify.web.service.pipeline.ReportExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class ReportExecutorImpl implements ReportExecutor {

    private final FortifySettingService fortifySettingService;

    public ReportExecutorImpl(FortifySettingService fortifySettingService) {
        this.fortifySettingService = fortifySettingService;
    }

    private String getReportGeneratorPath() {
        FortifySetting setting = fortifySettingService.getFortifySetting();
        if (setting == null || setting.getReportGeneratorExecutable() == null) {
            throw new IllegalStateException("Fortify ReportGenerator executable path is not configured.");
        }
        return setting.getReportGeneratorExecutable();
    }

    @Override
    public void generatePdf(String buildId, Path workspace) throws IOException, InterruptedException {
        log.info("Generating PDF report for buildId: {}", buildId);
        FortifySetting setting = fortifySettingService.getFortifySetting();
        if (setting == null || setting.getPdfOutputDirectory() == null) {
            throw new IllegalStateException("Fortify PDF output directory is not configured.");
        }
        Path sourceFpr = Path.of(setting.getFprOutputDirectory()).resolve(buildId + ".fpr");
        Path reportPdf = Path.of(setting.getPdfOutputDirectory()).resolve(buildId + ".pdf");
        Files.createDirectories(reportPdf.getParent());

        executeCommand(workspace, getReportGeneratorPath(), "-format", "pdf", "-f", reportPdf.toString(), "-source", sourceFpr.toString());
    }

    @Override
    public void generateXml(String buildId, Path workspace) throws IOException, InterruptedException {
        log.info("Generating XML report for buildId: {}", buildId);
        FortifySetting setting = fortifySettingService.getFortifySetting();
        if (setting == null || setting.getXmlOutputDirectory() == null) {
            throw new IllegalStateException("Fortify XML output directory is not configured.");
        }
        Path sourceFpr = Path.of(setting.getFprOutputDirectory()).resolve(buildId + ".fpr");
        Path reportXml = Path.of(setting.getXmlOutputDirectory()).resolve(buildId + ".xml");
        Files.createDirectories(reportXml.getParent());

        executeCommand(workspace, getReportGeneratorPath(), "-format", "xml", "-f", reportXml.toString(), "-source", sourceFpr.toString());
    }

    private void executeCommand(Path workingDirectory, String... command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDirectory.toFile());
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("[ReportGenerator Process]: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command execution failed with exit code: " + exitCode);
        }
        log.info("Command executed successfully.");
    }
}
