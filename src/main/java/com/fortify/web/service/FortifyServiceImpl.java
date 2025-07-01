package com.fortify.web.service;

import com.fortify.web.exception.FortifyScanException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

@Service
public class FortifyServiceImpl implements FortifyService {

    private static final String GRADLEW_PATH = "./gradlew";
    private static final String WEB_DIR = "web";

    @Async("fortifyTaskExecutor")
    @Override
    public CompletableFuture<String> runFortifyScan(String buildId, String classpath, String filesToAnalyze) {
        try {
            // fortifyTranslate 먼저 실행
            String translateResult = runFortifyTranslate(buildId, classpath, filesToAnalyze).get(); // .get()으로 블로킹 호출
            if (translateResult.startsWith("Error:")) {
                throw new FortifyScanException("Error during Fortify Translate: " + translateResult);
            }

            // fortifyScan 실행
            ProcessBuilder processBuilder = new ProcessBuilder(GRADLEW_PATH, "fortifyScan", "-PbuildId=" + buildId);
            processBuilder.directory(new java.io.File(WEB_DIR));
            return CompletableFuture.completedFuture(executeCommand(processBuilder));
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Error during Fortify scan: " + e.getMessage());
        }
    }

    @Async("fortifyTaskExecutor")
    @Override
    public CompletableFuture<String> runFortifyClean(String buildId) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(GRADLEW_PATH, "fortifyClean", "-PbuildId=" + buildId);
            processBuilder.directory(new java.io.File(WEB_DIR));
            return CompletableFuture.completedFuture(executeCommand(processBuilder));
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Error during Fortify clean: " + e.getMessage());
        }
    }

    @Async("fortifyTaskExecutor")
    @Override
    public CompletableFuture<String> runFortifyTranslate(String buildId, String classpath, String filesToAnalyze) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    GRADLEW_PATH, "fortifyTranslate",
                    "-PbuildId=" + buildId,
                    "-Pclasspath=" + classpath,
                    "-PfilesToAnalyze=" + filesToAnalyze
            );
            processBuilder.directory(new java.io.File(WEB_DIR));
            return CompletableFuture.completedFuture(executeCommand(processBuilder));
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Error during Fortify translate: " + e.getMessage());
        }
    }

    private String executeCommand(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        output.append("\nExited with error code : ").append(exitCode).append("\n");

        if (exitCode != 0) {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                output.append(line).append("\n");
            }
            throw new FortifyScanException("Command execution failed with exit code " + exitCode + ": " + output.toString());
        }
        return output.toString();
    }
}

