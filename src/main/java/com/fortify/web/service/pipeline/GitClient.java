package com.fortify.web.service.pipeline;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.nio.file.Path;

public interface GitClient {
    void cloneRepository(String repoUrl, String branch, Path directory) throws GitAPIException, IOException;
}
