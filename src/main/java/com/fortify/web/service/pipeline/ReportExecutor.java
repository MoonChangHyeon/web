package com.fortify.web.service.pipeline;

import java.io.IOException;
import java.nio.file.Path;

public interface ReportExecutor {
    void generatePdf(String buildId, Path workspace) throws IOException, InterruptedException;
    void generateXml(String buildId, Path workspace) throws IOException, InterruptedException;
}
