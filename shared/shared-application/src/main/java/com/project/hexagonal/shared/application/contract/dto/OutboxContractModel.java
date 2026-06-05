package com.project.hexagonal.shared.application.contract.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record OutboxContractModel(UUID id,
                                  UUID eventId,
                                  String eventType,
                                  int eventVersion,
                                  String eventPayload,
                                  String aggregateType,
                                  UUID aggregateId,
                                  String status,
                                  int retryCount,
                                  int maxRetries,
                                  String lastError,
                                  Instant createdAt,
                                  Instant publishedAt,
                                  Instant processedAt,
                                  UUID deadLetterLogId,
                                  boolean batchProcessed) {


}
