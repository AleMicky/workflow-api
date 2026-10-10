package com.atenea.workflowapi.dto;

public record ProcessDefinitionResponse(
        String id,
        String key,
        String name,
        int version,
        String deploymentId,
        boolean suspended
) {
}