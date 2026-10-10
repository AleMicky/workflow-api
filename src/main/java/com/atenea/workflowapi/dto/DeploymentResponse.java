package com.atenea.workflowapi.dto;

public record DeploymentResponse(
        String deploymentId,
        String processDefinitionId,
        String processKey,
        String processName,
        int version
) {
}