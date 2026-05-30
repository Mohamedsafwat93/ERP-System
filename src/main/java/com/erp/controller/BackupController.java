package com.erp.controller;

import com.erp.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
@Slf4j
public class BackupController {

    private final BackupService backupService;

    @PostMapping("/now")
    public ResponseEntity<String> backupNow() {
        try {
            backupService.manualBackup();
            return ResponseEntity.ok("Backup completed successfully");
        } catch (Exception e) {
            log.error("Backup failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Backup failed: " + e.getMessage());
        }
    }
}