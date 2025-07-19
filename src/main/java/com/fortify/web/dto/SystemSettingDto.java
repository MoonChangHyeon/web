package com.fortify.web.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingDto {
    private Long id;
    private String serverTimezone;
    private String serverLanguage;
    private String dbType;
    private String dbHost;
    private Integer dbPort;
    private String dbUser;
    private String dbPassword;
    private String dbName;
    private String fileStoragePath;
    private String logLevel;
    private String logPath;
    private Integer maxFileUploadSize;
    private Boolean smtpEnabled;
    private String smtpHost;
    private Integer smtpPort;
    private String smtpUser;
    private String smtpPassword;
    private String smtpSender;
}
