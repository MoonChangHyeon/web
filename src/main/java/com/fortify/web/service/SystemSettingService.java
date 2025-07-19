package com.fortify.web.service;

import com.fortify.web.domain.SystemSetting;
import com.fortify.web.dto.SystemSettingDto;
import com.fortify.web.repository.SystemSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    public SystemSettingService(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    @Transactional(readOnly = true)
    public SystemSettingDto getSystemSettings() {
        // Assuming there's only one row for system settings, typically with ID 1
        Optional<SystemSetting> settings = systemSettingRepository.findById(1L);
        return settings.map(this::convertToDto).orElseGet(() -> {
            // If no settings exist, return a DTO with default values or nulls
            SystemSetting defaultSettings = new SystemSetting();
            // Set default values if necessary, based on your configuration.json
            defaultSettings.setServerTimezone("Asia/Seoul");
            defaultSettings.setServerLanguage("ko");
            defaultSettings.setDbType("MariaDB");
            defaultSettings.setDbPort(3306);
            defaultSettings.setLogLevel("INFO");
            defaultSettings.setMaxFileUploadSize(200);
            defaultSettings.setSmtpEnabled(false);
            defaultSettings.setSmtpPort(587);
            return convertToDto(defaultSettings);
        });
    }

    @Transactional
    public SystemSettingDto saveSystemSettings(SystemSettingDto dto) {
        SystemSetting settings = systemSettingRepository.findById(1L).orElse(new SystemSetting());
        if (settings.getId() == null) {
            settings.setId(1L); // Ensure ID is 1 for the single settings row
        }
        updateEntityFromDto(settings, dto);
        SystemSetting savedSettings = systemSettingRepository.save(settings);
        return convertToDto(savedSettings);
    }

    private SystemSettingDto convertToDto(SystemSetting entity) {
        SystemSettingDto dto = new SystemSettingDto();
        dto.setId(entity.getId());
        dto.setServerTimezone(entity.getServerTimezone());
        dto.setServerLanguage(entity.getServerLanguage());
        dto.setDbType(entity.getDbType());
        dto.setDbHost(entity.getDbHost());
        dto.setDbPort(entity.getDbPort());
        dto.setDbUser(entity.getDbUser());
        dto.setDbPassword(entity.getDbPassword());
        dto.setDbName(entity.getDbName());
        dto.setFileStoragePath(entity.getFileStoragePath());
        dto.setLogLevel(entity.getLogLevel());
        dto.setLogPath(entity.getLogPath());
        dto.setMaxFileUploadSize(entity.getMaxFileUploadSize());
        dto.setSmtpEnabled(entity.getSmtpEnabled());
        dto.setSmtpHost(entity.getSmtpHost());
        dto.setSmtpPort(entity.getSmtpPort());
        dto.setSmtpUser(entity.getSmtpUser());
        dto.setSmtpPassword(entity.getSmtpPassword());
        dto.setSmtpSender(entity.getSmtpSender());
        return dto;
    }

    private void updateEntityFromDto(SystemSetting entity, SystemSettingDto dto) {
        entity.setServerTimezone(dto.getServerTimezone());
        entity.setServerLanguage(dto.getServerLanguage());
        entity.setDbType(dto.getDbType());
        entity.setDbHost(dto.getDbHost());
        entity.setDbPort(dto.getDbPort());
        entity.setDbUser(dto.getDbUser());
        entity.setDbPassword(dto.getDbPassword());
        entity.setDbName(dto.getDbName());
        entity.setFileStoragePath(dto.getFileStoragePath());
        entity.setLogLevel(dto.getLogLevel());
        entity.setLogPath(dto.getLogPath());
        entity.setMaxFileUploadSize(dto.getMaxFileUploadSize());
        entity.setSmtpEnabled(dto.getSmtpEnabled());
        entity.setSmtpHost(dto.getSmtpHost());
        entity.setSmtpPort(dto.getSmtpPort());
        entity.setSmtpUser(dto.getSmtpUser());
        entity.setSmtpPassword(dto.getSmtpPassword());
        entity.setSmtpSender(dto.getSmtpSender());
    }
}
