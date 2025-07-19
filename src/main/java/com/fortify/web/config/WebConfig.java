package com.fortify.web.config;

import com.fortify.web.domain.FortifySetting;
import com.fortify.web.service.FortifySettingService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final FortifySettingService fortifySettingService;

    public WebConfig(FortifySettingService fortifySettingService) {
        this.fortifySettingService = fortifySettingService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        FortifySetting setting = fortifySettingService.getFortifySetting();
        if (setting != null) {
            List<String> resourceLocations = new ArrayList<>();
            if (setting.getPdfOutputDirectory() != null) {
                resourceLocations.add("file:" + setting.getPdfOutputDirectory() + "/");
            }
            if (setting.getXmlOutputDirectory() != null) {
                resourceLocations.add("file:" + setting.getXmlOutputDirectory() + "/");
            }

            if (!resourceLocations.isEmpty()) {
                registry.addResourceHandler("/reports/**")
                        .addResourceLocations(resourceLocations.toArray(new String[0]));
            }
        }

        registry.addResourceHandler("/node_modules/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/", "file:node_modules/");
    }
}
