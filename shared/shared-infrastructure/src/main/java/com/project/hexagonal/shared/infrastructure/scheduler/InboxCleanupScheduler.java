package com.project.hexagonal.shared.infrastructure.scheduler;

import com.project.hexagonal.shared.infrastructure.persistence.repository.InboxJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class InboxCleanupScheduler {

    private final InboxJpaRepository repository;

    private static final int RETENTION_DAYS = 30;

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldProcessedEvents() {
        try {
            Instant cutoffDate = Instant.now().minusSeconds(RETENTION_DAYS * 86400L);

            long deletedCount = repository.deleteByProcessedTrueAndProcessedAtBefore(cutoffDate);

            if (deletedCount > 0) {
                log.info("Cleaned up {} old inbox entries (older than {} days)", deletedCount, RETENTION_DAYS);
            } else {
                log.debug("No inbox entries to clean up (retention period: {} days)", RETENTION_DAYS);
            }
        } catch (Exception e) {
            log.error("Inbox cleanup failed", e);
        }
    }
}
