package com.fortify.web.service.pipeline.impl;

import com.fortify.web.service.pipeline.ReportExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class ReportExecutorImpl implements ReportExecutor {

    private final String reportGeneratorPath;

    public ReportExecutorImpl(@Value("${fortify.report-generator.executable.path}") String reportGeneratorPath) {
        this.reportGeneratorPath = reportGeneratorPath;
    }

    @Override
    public void generatePdf(String buildId, Path workspace) throws IOException, InterruptedException {
        log.info("Generating PDF report for buildId: {}", buildId);
        Path sourceFpr = workspace.resolve("results").resolve(buildId + ".fpr");
        Path reportPdf = workspace.resolve("report").resolve(buildId + ".pdf");
        Files.createDirectories(reportPdf.getParent());

        executeCommand(workspace, reportGeneratorPath, "-format", "pdf", "-f", reportPdf.toString(), "-source", sourceFpr.toString());
    }

    @Override
    public void generateXml(String buildId, Path workspace) throws IOException, InterruptedException {
        log.info("Generating XML report for buildId: {}", buildId);
        Path sourceFpr = workspace.resolve("results").resolve(buildId + ".fpr");
        Path reportXml = workspace.resolve("report").resolve(buildId + ".xml");
        Files.createDirectories(reportXml.getParent());

        executeCommand(workspace, reportGeneratorPath, "-format", "xml", "-f", reportXml.toString(), "-source", sourceFpr.toString());
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
