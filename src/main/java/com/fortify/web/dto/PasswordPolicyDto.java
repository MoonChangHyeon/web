package com.fortify.web.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordPolicyDto {
    private Integer minLength;
    private Boolean requireUppercase;
    private Boolean requireNumber;
    private Boolean requireSpecial;
}
