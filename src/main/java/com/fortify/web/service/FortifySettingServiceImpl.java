package com.fortify.web.service;

import com.fortify.web.domain.FortifySetting;
import com.fortify.web.repository.FortifySettingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files; // Import Files
import java.nio.file.Path;
import java.nio.file.Paths; // Import Paths
import java.util.List;
import java.util.ArrayList;

@Service
@Transactional
public class FortifySettingServiceImpl implements FortifySettingService {

    private final FortifySettingRepository fortifySettingRepository;

    public FortifySettingServiceImpl(FortifySettingRepository fortifySettingRepository) {
        this.fortifySettingRepository = fortifySettingRepository;
    }

    @PostConstruct
    public void init() {
        // 애플리케이션 시작 시 Fortify 설정이 없으면 기본값 저장
        if (fortifySettingRepository.count() == 0) {
            FortifySetting setting = new FortifySetting();
            setting.setFortifyScaPath("/opt/fortify/Fortify_SCA_and_Apps_23.2.0"); // 예시 기본값
            setting.setFortifyToolsPath("/opt/fortify/Fortify_SCA_and_Apps_23.2.0"); // 예시 기본값
            setting.setResultOutputDirectory("./results"); // Added default value for resultOutputDirectory
            setting.setDefaultReportTemplate(""); // 기본 템플릿 없음
            setting.setDefaultReportUser(""); // 기본 사용자 없음
            setting.setDefaultReportShowRemoved(false);
            setting.setDefaultReportShowSuppressed(false);
            setting.setDefaultReportShowHidden(false);
            setting.setDefaultReportFilterSet(""); // 기본 필터셋 없음
            calculateAndSetDerivedPaths(setting);
            fortifySettingRepository.save(setting);
        }
    }

    @Override
    public FortifySetting getFortifySetting() {
        // 항상 하나의 설정만 존재한다고 가정하고 첫 번째 설정을 반환
        return fortifySettingRepository.findAll().stream().findFirst().orElse(null);
    }

    @Override
    public FortifySetting saveFortifySetting(FortifySetting fortifySetting) {
        // 기존 설정이 있다면 ID를 유지하여 업데이트
        if (fortifySettingRepository.count() > 0 && fortifySetting.getId() == null) {
            FortifySetting existingSetting = fortifySettingRepository.findAll().stream().findFirst().orElse(null);
            if (existingSetting != null) {
                fortifySetting.setId(existingSetting.getId());
            }
        }
        calculateAndSetDerivedPaths(fortifySetting);
        // Create resultOutputDirectory if it doesn't exist
        if (fortifySetting.getResultOutputDirectory() != null && !fortifySetting.getResultOutputDirectory().isBlank()) {
            createDirectoryIfNotExist(fortifySetting.getResultOutputDirectory());
        }
        return fortifySettingRepository.save(fortifySetting);
    }

    @Override
    public List<String> getReportTemplateFiles() {
        FortifySetting setting = getFortifySetting();
        if (setting == null || setting.getReportTemplatesDir() == null || setting.getReportTemplatesDir().isEmpty()) {
            return List.of();
        }

        java.io.File templateDir = new java.io.File(setting.getReportTemplatesDir());
        if (!templateDir.exists() || !templateDir.isDirectory()) {
            return List.of();
        }

        List<String> templateFiles = new java.util.ArrayList<>();
        for (java.io.File file : templateDir.listFiles()) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {
                templateFiles.add(file.getName());
            }
        }
        return templateFiles;
    }

    @Override
    public void updateReportTemplateRefinement(String templateFileName, String filterValue) {
        FortifySetting setting = getFortifySetting();
        if (setting == null || setting.getReportTemplatesDir() == null || setting.getReportTemplatesDir().isEmpty()) {
            throw new IllegalArgumentException("Fortify settings or report templates directory not configured.");
        }

        File templateFile = new File(setting.getReportTemplatesDir(), templateFileName);
        if (!templateFile.exists() || !templateFile.isFile()) {
            throw new IllegalArgumentException("Report template file not found: " + templateFileName);
        }

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(templateFile);

            NodeList refinementList = doc.getElementsByTagName("Refinement");
            if (refinementList.getLength() > 0) {
                Element refinementElement = (Element) refinementList.item(0);
                refinementElement.setTextContent(filterValue);
            } else {
                // If <Refinement> tag doesn't exist, you might want to create it or log a warning.
                // For now, let's just log a warning.
                System.out.println("Warning: <Refinement> tag not found in " + templateFileName);
            }

            // Write the updated XML back to the file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(templateFile);
            transformer.transform(source, result);

        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            throw new RuntimeException("Error updating report template refinement: " + e.getMessage(), e);
        }
    }

    private void createDirectoryIfNotExist(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("Directory created: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to create directory: " + directoryPath + " - " + e.getMessage());
            // Optionally, throw a custom exception or handle more gracefully
        }
    }

    private void calculateAndSetDerivedPaths(FortifySetting setting) {
        // Only update if the base path is provided. Otherwise, leave existing derived path.
        if (setting.getFortifyScaPath() != null && !setting.getFortifyScaPath().isEmpty()) {
            setting.setSourceanalyzerExecutable(setting.getFortifyScaPath() + "/bin/sourceanalyzer");
            setting.setScaExternalMetadataDir(setting.getFortifyScaPath() + "/Core/config/ExternalMetadata");
            setting.setScaPropertiesDir(setting.getFortifyScaPath() + "/Core/config");
        }

        if (setting.getFortifyToolsPath() != null && !setting.getFortifyToolsPath().isEmpty()) {
            setting.setReportGeneratorExecutable(setting.getFortifyToolsPath() + "/bin/ReportGenerator");
            String reportTemplatesDir = setting.getFortifyToolsPath() + "/Core/config/reports";
            setting.setReportTemplatesDir(reportTemplatesDir);

            java.io.File defaultReportFile = new java.io.File(reportTemplatesDir, "DefaultReportDefinition.xml");
            if (defaultReportFile.exists() && defaultReportFile.isFile()) {
                setting.setDefaultReportTemplate("DefaultReportDefinition.xml");
            } else {
                // Only clear if it was specifically "DefaultReportDefinition.xml" and the file is gone.
                // Otherwise, leave whatever was manually set.
                if ("DefaultReportDefinition.xml".equals(setting.getDefaultReportTemplate())) {
                    setting.setDefaultReportTemplate("");
                }
            }
        }

        // resultOutputDirectory를 기반으로 fpr, xml, pdf 출력 디렉토리 설정 및 생성
        if (setting.getResultOutputDirectory() != null && !setting.getResultOutputDirectory().isBlank()) {
            Path resultRoot = Paths.get(setting.getResultOutputDirectory());
            setting.setFprOutputDirectory(resultRoot.resolve("fpr").toString());
            setting.setPdfOutputDirectory(resultRoot.resolve("pdf").toString());
            setting.setXmlOutputDirectory(resultRoot.resolve("xml").toString());

            // 파생된 디렉토리 생성
            createDirectoryIfNotExist(setting.getFprOutputDirectory());
            createDirectoryIfNotExist(setting.getPdfOutputDirectory());
            createDirectoryIfNotExist(setting.getXmlOutputDirectory());
        }
    }
}
