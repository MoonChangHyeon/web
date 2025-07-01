package com.fortify.web.repository;

import com.fortify.web.domain.FortifySetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FortifySettingRepository extends JpaRepository<FortifySetting, Long> {
}
