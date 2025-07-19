package com.fortify.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProjectDto {

    private Long id;

    @NotBlank(message = "프로젝트 이름은 필수입니다.")
    private String projectName;

    @NotBlank(message = "Git Repository URL은 필수입니다.")
    private String repoUrl;

    private String branch = "main";

    private String credentialsId;

    private String projectSubdirectory = "."; // 프로젝트 하위 디렉토리 (기본값: .)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
