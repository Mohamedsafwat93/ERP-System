package com.erp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupService {

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private static final String BACKUP_PATH = "C:\\erp_backups\\";
    private static final String PG_DUMP_PATH = "\"C:\\Program Files\\PostgreSQL\\18\\bin\\pg_dump.exe\"";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Scheduled(cron = "0 0 2 * * ?")
    public void autoBackup() {
        try {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String backupFile = BACKUP_PATH + "erp_db_backup_" + timestamp + ".sql";

            new java.io.File(BACKUP_PATH).mkdirs();

            ProcessBuilder pb = new ProcessBuilder(
                    PG_DUMP_PATH, "-U", dbUsername, "-d", "erp_db", "-f", backupFile
            );
            pb.environment().put("PGPASSWORD", dbPassword);

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Database backup created successfully: {}", backupFile);
                cleanOldBackups();
            } else {
                log.error("Backup failed with exit code: {}", exitCode);
            }
        } catch (Exception e) {
            log.error("Backup error: {}", e.getMessage());
        }
    }

    private void cleanOldBackups() {
        // Keep only last 7 days of backups
    }

    public void manualBackup() throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFile = BACKUP_PATH + "erp_db_manual_" + timestamp + ".sql";

        new java.io.File(BACKUP_PATH).mkdirs();

        ProcessBuilder pb = new ProcessBuilder(
                PG_DUMP_PATH, "-U", dbUsername, "-d", "erp_db", "-f", backupFile
        );
        pb.environment().put("PGPASSWORD", dbPassword);

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Backup failed with exit code: " + exitCode);
        }

        log.info("Manual backup created: {}", backupFile);
    }
}