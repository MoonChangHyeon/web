package com.fortify.web.service.pipeline.impl;

import com.fortify.web.service.pipeline.WorkspaceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class WorkspaceManagerImpl implements WorkspaceManager {

    private final Path rootLocation;

    public WorkspaceManagerImpl(@Value("${fortify.workspace.root:/var/fortify/workspaces}") String workspaceRoot) {
        this.rootLocation = Paths.get(workspaceRoot);
    }

    @Override
    public Path createWorkspace(String jobId) throws IOException {
        Path workspacePath = rootLocation.resolve(jobId);
        // 기존 디렉터리가 있으면 삭제
        if (Files.exists(workspacePath)) {
            cleanupWorkspace(jobId);
        }
        Files.createDirectories(workspacePath);
        return workspacePath;
    }

    @Override
    public void cleanupWorkspace(String jobId) throws IOException {
        Path workspacePath = rootLocation.resolve(jobId);
        FileSystemUtils.deleteRecursively(workspacePath);
    }
}
