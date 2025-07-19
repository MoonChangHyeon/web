package com.fortify.web.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SecuritySettingDto {
    private Long id;
    private String adminIps;
    private List<String> rolePolicy;
    private String twoFactorAuth;
    private PasswordPolicyDto passwordPolicy;
    private List<ApiTokenDto> apiTokens;
    private Integer auditLogRetentionDays;
}
