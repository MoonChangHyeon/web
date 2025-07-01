package com.fortify.web.service;

import com.fortify.web.dto.AnalysisRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisServiceImpl.class);
    // TODO: 이 경로는 실제 환경에 맞게 설정 파일(application.properties)이나 DB에서 관리하는 것이 좋습니다.
    private static final String SOURCE_ANALYZER_PATH = "/opt/fortify/bin/sourceanalyzer";

    @Override
    public String runSourceAnalyzer(AnalysisRequestDto requestDto) throws IOException, InterruptedException {
        // DB에서 명령어 형식(템플릿)을 불러오는 로직을 여기에 추가할 수 있습니다.
        // 예: String commandTemplate = commandRepository.findByName("sourceanalyzer").getTemplate();

        List<String> command = new ArrayList<>();
        command.add(SOURCE_ANALYZER_PATH);
        command.add("-b");
        command.add(requestDto.getBuildId());
        command.add("-cp");
        command.add(requestDto.getClasspath());
        command.add(requestDto.getFiles());

        logger.info("Executing command: {}", String.join(" ", command));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 에러 스트림을 표준 출력으로 합칩니다.

        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                logger.debug(line); // 실시간 로그 출력
            }
        }

        // 프로세스가 끝날 때까지 최대 10분 대기
        if (!process.waitFor(10, TimeUnit.MINUTES)) {
            process.destroyForcibly();
            throw new InterruptedException("분석 프로세스가 시간 초과되었습니다.");
        }

        int exitCode = process.exitValue();
        logger.info("Process exited with code: {}", exitCode);

        return "Exit Code: " + exitCode + "\nOutput:\n" + output;
    }
}