package com.fortify.web.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordPolicy {
    private Integer minLength;
    private Boolean requireUppercase;
    private Boolean requireNumber;
    private Boolean requireSpecial;
}
