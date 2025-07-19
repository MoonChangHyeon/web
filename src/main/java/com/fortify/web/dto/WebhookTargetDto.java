package com.fortify.web.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebhookTargetDto {
    private Long id;
    private String type;
    private String url;
    private Boolean enabled;
}
