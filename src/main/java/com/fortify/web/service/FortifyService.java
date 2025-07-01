package com.fortify.web.service;

import java.util.concurrent.CompletableFuture;

public interface FortifyService {
    CompletableFuture<String> runFortifyScan(String buildId, String classpath, String filesToAnalyze);
    CompletableFuture<String> runFortifyClean(String buildId);
    CompletableFuture<String> runFortifyTranslate(String buildId, String classpath, String filesToAnalyze);
}
