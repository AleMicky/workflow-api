package com.atenea.workflowapi.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelProcessInstanceRequest(
        @NotBlank(message = "El motivo de cancelación es obligatorio.")
        String reason
) {
}