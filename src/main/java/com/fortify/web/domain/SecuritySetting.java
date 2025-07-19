package com.fortify.web.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "security_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SecuritySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Assuming a single row with ID 1 for global settings

    @Column(name = "admin_ips")
    private String adminIps;

    @ElementCollection
    @CollectionTable(name = "security_setting_role_policy", joinColumns = @JoinColumn(name = "security_setting_id"))
    @Column(name = "role_policy")
    private List<String> rolePolicy = new ArrayList<>();

    @Column(name = "two_factor_auth")
    private String twoFactorAuth;

    @Embedded
    private PasswordPolicy passwordPolicy;

    @OneToMany(mappedBy = "securitySetting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApiToken> apiTokens = new ArrayList<>();

    @Column(name = "audit_log_retention_days")
    private Integer auditLogRetentionDays;

    public void addApiToken(ApiToken apiToken) {
        apiTokens.add(apiToken);
        apiToken.setSecuritySetting(this);
    }

    public void removeApiToken(ApiToken apiToken) {
        apiTokens.remove(apiToken);
        apiToken.setSecuritySetting(null);
    }
}
