package com.atenea.workflowapi.dto;

import java.util.Map;

public record CompleteTaskRequest(
        Map<String, Object> variables
) {
}