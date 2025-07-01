package com.fortify.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private Long id;

    @NotBlank(message = "메시지는 비어 있을 수 없습니다.")
    private String text;
}
