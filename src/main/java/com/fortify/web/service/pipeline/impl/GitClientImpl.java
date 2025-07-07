package com.fortify.web.service.pipeline.impl;

import com.fortify.web.service.pipeline.GitClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Service
public class GitClientImpl implements GitClient {

    @Override
    public void cloneRepository(String repoUrl, String branch, Path directory) throws GitAPIException, IOException {
        log.info("Cloning repository: {} (branch: {}) into {}", repoUrl, branch, directory);

        Git.cloneRepository()
                .setURI(repoUrl)
                .setBranch(branch)
                .setDirectory(directory.toFile())
                .call();

        log.info("Successfully cloned repository: {}", repoUrl);
    }
}
