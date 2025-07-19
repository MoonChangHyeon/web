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
public class AutomationSettingDto {
    private Long id;
    private Boolean enableScheduler;
    private String analysisCron;
    private Boolean autoReportEnabled;
    private List<ReportReceiverDto> reportReceivers;
    private Boolean webhookEnabled;
    private List<WebhookTargetDto> webhookTargets;
    private Boolean backupEnabled;
    private String backupScheduleCron;
    private Integer backupRetentionDays;
}
