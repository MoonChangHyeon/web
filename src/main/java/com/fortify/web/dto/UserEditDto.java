package com.fortify.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserEditDto {
    private Long id;

    @NotBlank(message = "유저명은 필수입니다.")
    @Size(min = 3, max = 50, message = "유저명은 3자 이상 50자 이하여야 합니다.")
    private String username;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    private String status;

    private Set<Long> roleIds; // 선택된 역할 ID 목록
}
