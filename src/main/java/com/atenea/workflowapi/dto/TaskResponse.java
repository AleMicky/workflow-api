package com.atenea.workflowapi.dto;

import java.time.Instant;

public record TaskResponse(
        String id,
        String name,
        String assignee,
        String taskDefinitionKey,
        String processInstanceId,
        String processDefinitionId,
        Instant createdAt
) {
}