package com.atenea.workflowapi.dto;

public record TaskCompletionResponse(
        String taskId,
        String processInstanceId,
        boolean completed,
        boolean processEnded
) {
}