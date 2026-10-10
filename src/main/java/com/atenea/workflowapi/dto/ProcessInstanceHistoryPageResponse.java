package com.atenea.workflowapi.dto;

import java.util.List;

public record ProcessInstanceHistoryPageResponse(
        List<ProcessInstanceHistoryResponse> items,
        long total,
        int page,
        int size
) {
}