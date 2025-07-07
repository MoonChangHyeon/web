package com.fortify.web.service.pipeline;

import java.io.IOException;
import java.nio.file.Path;

public interface WorkspaceManager {
    Path createWorkspace(String jobId) throws IOException;
    void cleanupWorkspace(String jobId) throws IOException;
}
