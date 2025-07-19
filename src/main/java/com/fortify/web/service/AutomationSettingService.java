package com.fortify.web.service;

import com.fortify.web.domain.AutomationSetting;
import com.fortify.web.domain.ReportReceiver;
import com.fortify.web.domain.WebhookTarget;
import com.fortify.web.dto.AutomationSettingDto;
import com.fortify.web.dto.ReportReceiverDto;
import com.fortify.web.dto.WebhookTargetDto;
import com.fortify.web.repository.AutomationSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AutomationSettingService {

    private final AutomationSettingRepository automationSettingRepository;

    public AutomationSettingService(AutomationSettingRepository automationSettingRepository) {
        this.automationSettingRepository = automationSettingRepository;
    }

    @Transactional(readOnly = true)
    public AutomationSettingDto getAutomationSettings() {
        Optional<AutomationSetting> settings = automationSettingRepository.findById(1L);
        return settings.map(this::convertToDto).orElseGet(() -> {
            AutomationSetting defaultSettings = new AutomationSetting();
            defaultSettings.setEnableScheduler(false);
            defaultSettings.setAutoReportEnabled(false);
            defaultSettings.setWebhookEnabled(false);
            defaultSettings.setBackupEnabled(false);
            defaultSettings.setBackupRetentionDays(30);
            return convertToDto(defaultSettings);
        });
    }

    @Transactional
    public AutomationSettingDto saveAutomationSettings(AutomationSettingDto dto) {
        AutomationSetting settings = automationSettingRepository.findById(1L).orElse(new AutomationSetting());
        if (settings.getId() == null) {
            settings.setId(1L);
        }
        updateEntityFromDto(settings, dto);
        AutomationSetting savedSettings = automationSettingRepository.save(settings);
        return convertToDto(savedSettings);
    }

    private AutomationSettingDto convertToDto(AutomationSetting entity) {
        AutomationSettingDto dto = new AutomationSettingDto();
        dto.setId(entity.getId());
        dto.setEnableScheduler(entity.getEnableScheduler());
        dto.setAnalysisCron(entity.getAnalysisCron());
        dto.setAutoReportEnabled(entity.getAutoReportEnabled());
        dto.setReportReceivers(entity.getReportReceivers().stream()
                .map(receiver -> new ReportReceiverDto(receiver.getId(), receiver.getEmail()))
                .collect(Collectors.toList()));
        dto.setWebhookEnabled(entity.getWebhookEnabled());
        dto.setWebhookTargets(entity.getWebhookTargets().stream()
                .map(target -> new WebhookTargetDto(target.getId(), target.getType(), target.getUrl(), target.getEnabled()))
                .collect(Collectors.toList()));
        dto.setBackupEnabled(entity.getBackupEnabled());
        dto.setBackupScheduleCron(entity.getBackupScheduleCron());
        dto.setBackupRetentionDays(entity.getBackupRetentionDays());
        return dto;
    }

    private void updateEntityFromDto(AutomationSetting entity, AutomationSettingDto dto) {
        entity.setEnableScheduler(dto.getEnableScheduler());
        entity.setAnalysisCron(dto.getAnalysisCron());
        entity.setAutoReportEnabled(dto.getAutoReportEnabled());

        // Update Report Receivers
        entity.getReportReceivers().clear();
        if (dto.getReportReceivers() != null) {
            dto.getReportReceivers().forEach(receiverDto -> {
                ReportReceiver receiver = new ReportReceiver();
                receiver.setId(receiverDto.getId());
                receiver.setEmail(receiverDto.getEmail());
                entity.addReportReceiver(receiver);
            });
        }

        entity.setWebhookEnabled(dto.getWebhookEnabled());

        // Update Webhook Targets
        entity.getWebhookTargets().clear();
        if (dto.getWebhookTargets() != null) {
            dto.getWebhookTargets().forEach(targetDto -> {
                WebhookTarget target = new WebhookTarget();
                target.setId(targetDto.getId());
                target.setType(targetDto.getType());
                target.setUrl(targetDto.getUrl());
                target.setEnabled(targetDto.getEnabled());
                entity.addWebhookTarget(target);
            });
        }

        entity.setBackupEnabled(dto.getBackupEnabled());
        entity.setBackupScheduleCron(dto.getBackupScheduleCron());
        entity.setBackupRetentionDays(dto.getBackupRetentionDays());
    }
}
