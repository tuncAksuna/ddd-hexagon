package com.project.hexagonal.shared.infrastructure.persistence.adapter;

import com.project.hexagonal.shared.infrastructure.persistence.entity.EventFailureLogEntity;
import com.project.hexagonal.shared.infrastructure.persistence.enums.EventFailureStatus;
import com.project.hexagonal.shared.infrastructure.persistence.repository.EventFailureLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventFailureLogAdapter {

    private final EventFailureLogJpaRepository repository;

    public void log(String eventType, String eventData, Exception e) {
        try {
            String stackTrace = getStackTrace(e);
            EventFailureLogEntity failureLog = EventFailureLogEntity.builder()
                    .eventType(eventType)
                    .eventData(eventData)
                    .errorMessage(e.getMessage())
                    .stackTrace(stackTrace)
                    .status(EventFailureStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();
            repository.save(failureLog);
        } catch (Exception ex) {
            log.error("Failed to log event failure to DLQ", ex);
        }
    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
