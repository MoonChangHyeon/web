package com.fortify.web.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "automation_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutomationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Assuming a single row with ID 1 for global settings

    @Column(name = "enable_scheduler")
    private Boolean enableScheduler;

    @Column(name = "analysis_cron")
    private String analysisCron;

    @Column(name = "auto_report_enabled")
    private Boolean autoReportEnabled;

    @OneToMany(mappedBy = "automationSetting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportReceiver> reportReceivers = new ArrayList<>();

    @Column(name = "webhook_enabled")
    private Boolean webhookEnabled;

    @OneToMany(mappedBy = "automationSetting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebhookTarget> webhookTargets = new ArrayList<>();

    @Column(name = "backup_enabled")
    private Boolean backupEnabled;

    @Column(name = "backup_schedule_cron")
    private String backupScheduleCron;

    @Column(name = "backup_retention_days")
    private Integer backupRetentionDays;

    public void addReportReceiver(ReportReceiver receiver) {
        reportReceivers.add(receiver);
        receiver.setAutomationSetting(this);
    }

    public void removeReportReceiver(ReportReceiver receiver) {
        reportReceivers.remove(receiver);
        receiver.setAutomationSetting(null);
    }

    public void addWebhookTarget(WebhookTarget target) {
        webhookTargets.add(target);
        target.setAutomationSetting(this);
    }

    public void removeWebhookTarget(WebhookTarget target) {
        webhookTargets.remove(target);
        target.setAutomationSetting(null);
    }
}
