package com.fortify.web.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "api_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String value;
    private Boolean enabled;
    private String scope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_setting_id")
    private SecuritySetting securitySetting;
}
