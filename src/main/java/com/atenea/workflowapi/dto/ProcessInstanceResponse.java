package com.atenea.workflowapi.dto;

public record ProcessInstanceResponse(
        String id,
        String processDefinitionId,
        String businessKey,
        boolean ended,
        boolean suspended
) {
}