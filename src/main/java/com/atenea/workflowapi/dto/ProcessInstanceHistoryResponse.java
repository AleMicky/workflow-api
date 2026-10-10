package com.atenea.workflowapi.dto;

import java.time.Instant;

public record ProcessInstanceHistoryResponse(
        String id,
        String processDefinitionId,
        String processDefinitionKey,
        String businessKey,
        String status,
        Instant startTime,
        Instant endTime,
        Long durationInMillis,
        String deleteReason
) {
}