package com.atenea.workflowapi.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskClaimRequest(
        @NotBlank(message = "El usuario es obligatorio.")
        String userId
) {
}