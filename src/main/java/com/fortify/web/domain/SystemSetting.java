package com.fortify.web.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "system_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Assuming a single row with ID 1 for global settings

    // Web/Server Settings
    @Column(name = "server_timezone")
    private String serverTimezone;

    @Column(name = "server_language")
    private String serverLanguage;

    @Column(name = "db_type")
    private String dbType;

    @Column(name = "db_host")
    private String dbHost;

    @Column(name = "db_port")
    private Integer dbPort;

    @Column(name = "db_user")
    private String dbUser;

    @Column(name = "db_password")
    private String dbPassword;

    @Column(name = "db_name")
    private String dbName;

    @Column(name = "file_storage_path")
    private String fileStoragePath;

    @Column(name = "log_level")
    private String logLevel;

    @Column(name = "log_path")
    private String logPath;

    @Column(name = "max_file_upload_size")
    private Integer maxFileUploadSize;

    @Column(name = "smtp_enabled")
    private Boolean smtpEnabled;

    @Column(name = "smtp_host")
    private String smtpHost;

    @Column(name = "smtp_port")
    private Integer smtpPort;

    @Column(name = "smtp_user")
    private String smtpUser;

    @Column(name = "smtp_password")
    private String smtpPassword;

    @Column(name = "smtp_sender")
    private String smtpSender;
}
