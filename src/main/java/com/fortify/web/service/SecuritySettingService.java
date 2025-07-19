package com.fortify.web.service;

import com.fortify.web.domain.ApiToken;
import com.fortify.web.domain.PasswordPolicy;
import com.fortify.web.domain.SecuritySetting;
import com.fortify.web.dto.ApiTokenDto;
import com.fortify.web.dto.PasswordPolicyDto;
import com.fortify.web.dto.SecuritySettingDto;
import com.fortify.web.repository.SecuritySettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SecuritySettingService {

    private final SecuritySettingRepository securitySettingRepository;

    public SecuritySettingService(SecuritySettingRepository securitySettingRepository) {
        this.securitySettingRepository = securitySettingRepository;
    }

    @Transactional(readOnly = true)
    public SecuritySettingDto getSecuritySettings() {
        Optional<SecuritySetting> settings = securitySettingRepository.findById(1L);
        return settings.map(this::convertToDto).orElseGet(() -> {
            SecuritySetting defaultSettings = new SecuritySetting();
            defaultSettings.setTwoFactorAuth("none");
            defaultSettings.setAuditLogRetentionDays(90);
            defaultSettings.setRolePolicy(List.of("admin", "user"));
            defaultSettings.setPasswordPolicy(new PasswordPolicy(8, false, false, false));
            return convertToDto(defaultSettings);
        });
    }

    @Transactional
    public SecuritySettingDto saveSecuritySettings(SecuritySettingDto dto) {
        SecuritySetting settings = securitySettingRepository.findById(1L).orElse(new SecuritySetting());
        if (settings.getId() == null) {
            settings.setId(1L);
        }
        updateEntityFromDto(settings, dto);
        SecuritySetting savedSettings = securitySettingRepository.save(settings);
        return convertToDto(savedSettings);
    }

    private SecuritySettingDto convertToDto(SecuritySetting entity) {
        SecuritySettingDto dto = new SecuritySettingDto();
        dto.setId(entity.getId());
        dto.setAdminIps(entity.getAdminIps());
        dto.setRolePolicy(entity.getRolePolicy());
        dto.setTwoFactorAuth(entity.getTwoFactorAuth());
        if (entity.getPasswordPolicy() != null) {
            dto.setPasswordPolicy(new PasswordPolicyDto(
                    entity.getPasswordPolicy().getMinLength(),
                    entity.getPasswordPolicy().getRequireUppercase(),
                    entity.getPasswordPolicy().getRequireNumber(),
                    entity.getPasswordPolicy().getRequireSpecial()
            ));
        }
        dto.setApiTokens(entity.getApiTokens().stream()
                .map(token -> new ApiTokenDto(token.getId(), token.getName(), token.getValue(), token.getEnabled(), token.getScope()))
                .collect(Collectors.toList()));
        dto.setAuditLogRetentionDays(entity.getAuditLogRetentionDays());
        return dto;
    }

    private void updateEntityFromDto(SecuritySetting entity, SecuritySettingDto dto) {
        entity.setAdminIps(dto.getAdminIps());
        entity.setRolePolicy(dto.getRolePolicy());
        entity.setTwoFactorAuth(dto.getTwoFactorAuth());

        if (dto.getPasswordPolicy() != null) {
            if (entity.getPasswordPolicy() == null) {
                entity.setPasswordPolicy(new PasswordPolicy());
            }
            entity.getPasswordPolicy().setMinLength(dto.getPasswordPolicy().getMinLength());
            entity.getPasswordPolicy().setRequireUppercase(dto.getPasswordPolicy().getRequireUppercase());
            entity.getPasswordPolicy().setRequireNumber(dto.getPasswordPolicy().getRequireNumber());
            entity.getPasswordPolicy().setRequireSpecial(dto.getPasswordPolicy().getRequireSpecial());
        }

        // Update API Tokens
        entity.getApiTokens().clear();
        if (dto.getApiTokens() != null) {
            dto.getApiTokens().forEach(tokenDto -> {
                ApiToken apiToken = new ApiToken();
                apiToken.setId(tokenDto.getId()); // ID is set for existing tokens
                apiToken.setName(tokenDto.getName());
                apiToken.setValue(tokenDto.getValue());
                apiToken.setEnabled(tokenDto.getEnabled());
                apiToken.setScope(tokenDto.getScope());
                entity.addApiToken(apiToken);
            });
        }
        entity.setAuditLogRetentionDays(dto.getAuditLogRetentionDays());
    }
}
