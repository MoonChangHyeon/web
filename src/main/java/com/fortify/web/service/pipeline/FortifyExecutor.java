package com.fortify.web.service.pipeline;

import java.io.IOException;
import java.nio.file.Path;

public interface FortifyExecutor {
    void clean(String buildId, Path workspace) throws IOException, InterruptedException;
    void translate(String buildId, Path workspace) throws IOException, InterruptedException;
    void scan(String buildId, Path workspace) throws IOException, InterruptedException;
}
