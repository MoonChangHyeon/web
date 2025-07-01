package com.fortify.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalysisRequestDto {

    @NotBlank(message = "Build ID는 비워둘 수 없습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Build ID는 영문, 숫자, 밑줄(_), 하이픈(-), 점(.)만 사용할 수 있습니다.")
    private String buildId;

    // 참고: Classpath와 Files는 경로 문자열이므로 복잡한 패턴을 가질 수 있습니다.
    // 실제 환경에서는 더욱 정교한 검증이 필요합니다.
    @NotBlank(message = "Classpath는 비워둘 수 없습니다.")
    private String classpath;

    @NotBlank(message = "분석할 파일 경로는 비워둘 수 없습니다.")
    private String files;

}