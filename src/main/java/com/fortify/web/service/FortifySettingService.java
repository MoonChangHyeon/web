package com.fortify.web.service;

import com.fortify.web.domain.FortifySetting;

import java.util.List;

public interface FortifySettingService {
    FortifySetting getFortifySetting();
    FortifySetting saveFortifySetting(FortifySetting fortifySetting);
    List<String> getReportTemplateFiles();
    void updateReportTemplateRefinement(String templateFileName, String filterValue);
}
