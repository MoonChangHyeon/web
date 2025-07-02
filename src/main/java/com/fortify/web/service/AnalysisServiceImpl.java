package com.fortify.web.service;

import com.fortify.web.domain.AnalysisRequest;
import com.fortify.web.domain.AnalysisStatus;
import com.fortify.web.domain.FortifySetting;
import com.fortify.web.repository.AnalysisRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final FortifySettingService fortifySettingService;
    private final AnalysisRequestRepository analysisRequestRepository;

    public AnalysisServiceImpl(FortifySettingService fortifySettingService, AnalysisRequestRepository analysisRequestRepository) {
        this.fortifySettingService = fortifySettingService;
        this.analysisRequestRepository = analysisRequestRepository;
    }

    @Override
    public void requestAnalysis(String buildId, MultipartFile file) throws Exception {
        FortifySetting setting = fortifySettingService.getFortifySetting();
        if (setting == null || setting.getMaxAnalysisFileSize() == null) {
            throw new Exception("분석 용량 제한이 설정되지 않았습니다. Fortify Settings에서 설정해주세요.");
        }

        long maxFileSize = setting.getMaxAnalysisFileSize() * 1024 * 1024; // MB to Bytes
        if (file.getSize() > maxFileSize) {
            throw new Exception("파일 크기가 분석 가능 용량(" + setting.getMaxAnalysisFileSize() + "MB)을 초과합니다.");
        }

        AnalysisRequest analysisRequest = new AnalysisRequest();
        analysisRequest.setBuildId(buildId);
        analysisRequest.setFileName(file.getOriginalFilename());
        analysisRequest.setFileSize(file.getSize());
        analysisRequest.setStatus(AnalysisStatus.PENDING);
        analysisRequestRepository.save(analysisRequest);

        // 비동기적으로 분석 요청 처리
        CompletableFuture.runAsync(() -> {
            Path targetFilePath = null;
            try {
                // 1. 업로드된 파일 저장
                String fortifyUploadsDirectory = setting.getFortifyUploadsDirectory();
                if (fortifyUploadsDirectory == null || fortifyUploadsDirectory.isEmpty()) {
                    throw new RuntimeException("Fortify Settings에 업로드 디렉토리가 설정되지 않았습니다.");
                }
                
                System.out.println("Fortify Uploads Directory: " + fortifyUploadsDirectory);
                Path uploadDir = Paths.get(fortifyUploadsDirectory);
                
                try {
                    Files.createDirectories(uploadDir);
                    System.out.println("Upload directory created/exists: " + uploadDir.toAbsolutePath());
                } catch (Exception e) {
                    System.err.println("Failed to create upload directory: " + uploadDir.toAbsolutePath() + ", Error: " + e.getMessage());
                    throw new RuntimeException("업로드 디렉토리 생성 실패: " + e.getMessage(), e);
                }

                targetFilePath = uploadDir.resolve(file.getOriginalFilename());
                System.out.println("Target file path: " + targetFilePath.toAbsolutePath());
                
                try {
                    file.transferTo(targetFilePath.toFile());
                    System.out.println("File uploaded successfully to: " + targetFilePath.toAbsolutePath());
                } catch (Exception e) {
                    System.err.println("Failed to upload file to: " + targetFilePath.toAbsolutePath() + ", Error: " + e.getMessage());
                    throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
                }

                // 상태 업데이트: QUEUED
                analysisRequest.setStatus(AnalysisStatus.QUEUED);
                analysisRequestRepository.save(analysisRequest);

                // 프로젝트 루트 디렉토리 (gradlew가 있는 곳)
                String projectRoot = System.getProperty("user.dir");
                File webDir = new File(projectRoot); // 프로젝트 루트가 /Users/munchanghyeon/Documents/Workspce/src/Fortify_SB/web/ 이므로 바로 사용

                String sourceanalyzerExecutable = setting.getSourceanalyzerExecutable();
                if (sourceanalyzerExecutable == null || sourceanalyzerExecutable.isEmpty()) {
                    throw new RuntimeException("Fortify Settings에 sourceanalyzer 실행 파일 경로가 설정되지 않았습니다.");
                }

                // 2. Fortify SCA Clean 실행 (이전 빌드 잔여물 제거)
                List<String> cleanCommand = new ArrayList<>();
                cleanCommand.add(sourceanalyzerExecutable);
                cleanCommand.add("-b");
                cleanCommand.add(buildId);
                cleanCommand.add("-clean");

                System.out.println("Executing Fortify Clean command: " + String.join(" ", cleanCommand));
                ProcessBuilder cleanProcessBuilder = new ProcessBuilder(cleanCommand);
                cleanProcessBuilder.directory(webDir); // 'web' 디렉토리에서 실행
                Process cleanProcess = cleanProcessBuilder.start();
                String cleanOutput = readProcessOutput(cleanProcess.getInputStream());
                String cleanError = readProcessOutput(cleanProcess.getErrorStream());
                int cleanExitCode = cleanProcess.waitFor();

                if (cleanExitCode != 0) {
                    System.err.println("Fortify Clean 실패 (종료 코드 " + cleanExitCode + ")\nStandard Output:\n" + cleanOutput + "\nError Output:\n" + cleanError);
                    // Clean 실패는 분석 실패로 이어지지 않으므로 예외를 던지지 않음
                }
                System.out.println("Fortify Clean Standard Output:\n" + cleanOutput);
                System.err.println("Fortify Clean Error Output:\n" + cleanError);

                // 2. Fortify SCA Translate 실행
                analysisRequest.setStatus(AnalysisStatus.BUILDING);
                analysisRequestRepository.save(analysisRequest);
                
                // sourceanalyzerExecutable은 이미 위에서 가져옴

                List<String> translateCommand = new ArrayList<>();
                translateCommand.add(sourceanalyzerExecutable);
                translateCommand.add("-b");
                translateCommand.add(buildId);
                // -cp 인자 제거 (단일 Java 파일 분석 시 불필요하거나, 별도의 클래스패스가 필요한 경우에만 추가)
                translateCommand.add(targetFilePath.toAbsolutePath().toString()); // 분석 대상 파일

                // Fortify 설정에서 메모리 설정 추가
                if (setting.getBuildMemory() != null && !setting.getBuildMemory().isEmpty()) {
                    translateCommand.add("-Xmx" + setting.getBuildMemory());
                }

                System.out.println("Executing Fortify Translate command: " + String.join(" ", translateCommand));
                ProcessBuilder translateProcessBuilder = new ProcessBuilder(translateCommand);
                translateProcessBuilder.directory(webDir); // 'web' 디렉토리에서 실행
                Process translateProcess = translateProcessBuilder.start();
                String translateOutput = readProcessOutput(translateProcess.getInputStream());
                String translateError = readProcessOutput(translateProcess.getErrorStream());
                int translateExitCode = translateProcess.waitFor();

                if (translateExitCode != 0) {
                    throw new RuntimeException("Fortify Translate 실패 (종료 코드 " + translateExitCode + ")\nStandard Output:\n" + translateOutput + "\nError Output:\n" + translateError);
                }
                System.out.println("Fortify Translate Standard Output:\n" + translateOutput);
                System.err.println("Fortify Translate Error Output:\n" + translateError);

                // 3. Fortify SCA Scan 실행
                analysisRequest.setStatus(AnalysisStatus.ANALYZING);
                analysisRequestRepository.save(analysisRequest);

                // Fortify Settings에서 설정된 리포트 출력 디렉토리 사용
                String reportOutputDirectory = setting.getReportOutputDirectory();
                if (reportOutputDirectory == null || reportOutputDirectory.isEmpty()) {
                    throw new RuntimeException("Fortify Settings에 리포트 출력 디렉토리가 설정되지 않았습니다.");
                }

                String fortifyOutputFpr = Paths.get(reportOutputDirectory, buildId + ".fpr").toAbsolutePath().toString();

                List<String> scanCommand = new ArrayList<>();
                scanCommand.add(sourceanalyzerExecutable);
                scanCommand.add("-b");
                scanCommand.add(buildId);
                scanCommand.add("-scan");
                scanCommand.add("-f");
                scanCommand.add(fortifyOutputFpr);

                System.out.println("Executing Fortify Scan command: " + String.join(" ", scanCommand));
                ProcessBuilder scanProcessBuilder = new ProcessBuilder(scanCommand);
                scanProcessBuilder.directory(webDir); // 'web' 디렉토리에서 실행
                Process scanProcess = scanProcessBuilder.start();
                String scanOutput = readProcessOutput(scanProcess.getInputStream());
                String scanError = readProcessOutput(scanProcess.getErrorStream());
                int scanExitCode = scanProcess.waitFor();

                if (scanExitCode != 0) {
                    throw new RuntimeException("Fortify Scan 실패 (종료 코드 " + scanExitCode + ")\nStandard Output:\n" + scanOutput + "\nError Output:\n" + scanError);
                }
                System.out.println("Fortify Scan Standard Output:\n" + scanOutput);
                System.err.println("Fortify Scan Error Output:\n" + scanError);

                // 4. FPR 파일을 최종 결과 폴더로 이동 (이미 fortifyOutputFpr에 지정되었으므로 이동 불필요)
                analysisRequest.setStatus(AnalysisStatus.COMPLETED);
                analysisRequestRepository.save(analysisRequest);

            } catch (Exception e) {
                analysisRequest.setStatus(AnalysisStatus.FAILED);
                analysisRequest.setErrorMessage("분석 실패: " + e.getMessage());
                analysisRequestRepository.save(analysisRequest);
            } finally {
                // 임시 파일 정리
                if (targetFilePath != null) {
                    try {
                        Files.deleteIfExists(targetFilePath);
                    } catch (Exception e) {
                        System.err.println("임시 파일 삭제 실패: " + e.getMessage());
                    }
                }
            }
        });
    }

    public List<AnalysisRequest> getAllAnalysisRequests() {
        return analysisRequestRepository.findAll();
    }

    private String readProcessOutput(java.io.InputStream inputStream) throws java.io.IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }
}