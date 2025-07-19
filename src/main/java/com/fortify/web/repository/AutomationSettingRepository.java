package com.fortify.web.repository;

import com.fortify.web.domain.AutomationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutomationSettingRepository extends JpaRepository<AutomationSetting, Long> {
}
