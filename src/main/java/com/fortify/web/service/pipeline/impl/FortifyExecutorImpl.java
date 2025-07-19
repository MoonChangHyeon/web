package com.fortify.web.service.pipeline.impl;

import com.fortify.web.domain.FortifySetting;
import com.fortify.web.service.FortifySettingService;
import com.fortify.web.service.pipeline.FortifyExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

@Slf4j
@Service
public class FortifyExecutorImpl implements FortifyExecutor {

    private final FortifySettingService fortifySettingService;

    public FortifyExecutorImpl(FortifySettingService fortifySettingService) {
        this.fortifySettingService = fortifySettingService;
    }

    private String getSourceanalyzerPath() {
        FortifySetting setting = fortifySettingService.getFortifySetting();
        if (setting == null || setting.getSourceanalyzerExecutable() == null) {
            throw new IllegalStateException("Fortify sourceanalyzer executable path is not configured.");
        }
        return setting.getSourceanalyzerExecutable();
    }

    @Override
    public void clean(String buildId, Path workspace) throws IOException, InterruptedException {
        log.info("Executing Fortify Clean for buildId: {}", buildId);
        executeCommand(workspace, getSourceanalyzerPath(), "-b", buildId, "-clean");
    }

    @Override
    public void translate(String buildId, Path workspace) throws IOException, InterruptedException {
        log.info("Executing Fortify Translate for buildId: {} on directory: {}", buildId, workspace);
        executeCommand(workspace, getSourceanalyzerPath(), "-b", buildId, workspace.toString());
    }

    @Override
    public void scan(String buildId, Path workspace) throws IOException, InterruptedException {
        log.info("Executing Fortify Scan for buildId: {}", buildId);
        FortifySetting setting = fortifySettingService.getFortifySetting();
        if (setting == null) {
            log.error("Fortify 설정을 데이터베이스에서 찾을 수 없습니다. 설정 페이지에서 Fortify 설정을 먼저 구성해주세요.");
            throw new IllegalStateException("Fortify 설정을 찾을 수 없습니다.");
        }
        if (setting.getFprOutputDirectory() == null || setting.getFprOutputDirectory().isBlank()) {
            log.error("Fortify FPR 출력 디렉토리가 설정되지 않았습니다. (설정 ID: {})", setting.getId());
            throw new IllegalStateException("Fortify FPR 출력 디렉토리가 설정되지 않았습니다. Fortify 설정을 확인해주세요.");
        }
        Path fprOutputDirectory = Path.of(setting.getFprOutputDirectory());
        Path fprPath = fprOutputDirectory.resolve(buildId + ".fpr");
        java.nio.file.Files.createDirectories(fprPath.getParent());
        executeCommand(workspace, getSourceanalyzerPath(), "-b", buildId, "-scan", "-f", fprPath.toString());
    }

    private void executeCommand(Path workingDirectory, String... command) throws IOException, InterruptedException {
        log.info("Executing command: {} in directory: {}", String.join(" ", command), workingDirectory);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDirectory.toFile());
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("[Fortify Process]: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command execution failed with exit code: " + exitCode);
        }
        log.info("Command executed successfully.");
    }
}
