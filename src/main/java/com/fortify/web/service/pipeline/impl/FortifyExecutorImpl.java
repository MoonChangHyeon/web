package com.fortify.web.service.pipeline.impl;

import com.fortify.web.service.pipeline.FortifyExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

@Slf4j
@Service
public class FortifyExecutorImpl implements FortifyExecutor {

    private final String sourceanalyzerPath;

    public FortifyExecutorImpl(@Value("${fortify.executable.path}") String sourceanalyzerPath) {
        this.sourceanalyzerPath = sourceanalyzerPath;
    }

    @Override
    public void clean(String buildId, Path workspace) throws IOException, InterruptedException {
        log.info("Executing Fortify Clean for buildId: {}", buildId);
        executeCommand(workspace, sourceanalyzerPath, "-b", buildId, "-clean");
    }

    @Override
    public void translate(String buildId, Path workspace) throws IOException, InterruptedException {
        log.info("Executing Fortify Translate for buildId: {}", buildId);
        // Assuming Gradle project
        executeCommand(workspace, sourceanalyzerPath, "-b", buildId, "touchless", "gradle", "clean", "build");
    }

    @Override
    public void scan(String buildId, Path workspace) throws IOException, InterruptedException {
        log.info("Executing Fortify Scan for buildId: {}", buildId);
        Path fprPath = workspace.resolve("results").resolve(buildId + ".fpr");
        java.nio.file.Files.createDirectories(fprPath.getParent());
        executeCommand(workspace, sourceanalyzerPath, "-b", buildId, "-scan", "-f", fprPath.toString());
    }

    private void executeCommand(Path workingDirectory, String... command) throws IOException, InterruptedException {
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
