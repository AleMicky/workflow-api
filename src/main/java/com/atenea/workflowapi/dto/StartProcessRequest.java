package com.atenea.workflowapi.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record StartProcessRequest(
        @NotBlank(message = "La clave del proceso es obligatoria.")
        String processKey,

        @NotBlank(message = "La clave de negocio es obligatoria.")
        String businessKey,

        Map<String, Object> variables
) {
}