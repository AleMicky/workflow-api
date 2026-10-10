package com.atenea.workflowapi.dto;

public record ProcessInstanceActionResponse(
        String processInstanceId,
        String status,
        String message
) {
}