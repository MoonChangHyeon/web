package com.fortify.web.controller;

import com.fortify.web.service.FortifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/fortify")
public class FortifyController {

    private final FortifyService fortifyService;

    public FortifyController(FortifyService fortifyService) {
        this.fortifyService = fortifyService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/scan")
    public CompletableFuture<ResponseEntity<String>> runScan(
            @RequestParam(defaultValue = "default_build_id") String buildId,
            @RequestParam(defaultValue = "") String classpath,
            @RequestParam(defaultValue = "src/main/java") String filesToAnalyze) {
        return fortifyService.runFortifyScan(buildId, classpath, filesToAnalyze)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(500).body("Error during Fortify scan: " + ex.getMessage()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/clean")
    public CompletableFuture<ResponseEntity<String>> runClean(@RequestParam(defaultValue = "default_build_id") String buildId) {
        return fortifyService.runFortifyClean(buildId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(500).body("Error during Fortify clean: " + ex.getMessage()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/translate")
    public CompletableFuture<ResponseEntity<String>> runTranslate(
            @RequestParam(defaultValue = "default_build_id") String buildId,
            @RequestParam(defaultValue = "") String classpath,
            @RequestParam(defaultValue = "src/main/java") String filesToAnalyze) {
        return fortifyService.runFortifyTranslate(buildId, classpath, filesToAnalyze)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(500).body("Error during Fortify translate: " + ex.getMessage()));
    }
}
