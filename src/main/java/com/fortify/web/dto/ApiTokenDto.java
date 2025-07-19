package com.fortify.web.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiTokenDto {
    private Long id;
    private String name;
    private String value;
    private Boolean enabled;
    private String scope;
}
