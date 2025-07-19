package com.fortify.web.repository;

import com.fortify.web.domain.SecuritySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecuritySettingRepository extends JpaRepository<SecuritySetting, Long> {
}
