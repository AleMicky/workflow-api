package com.atenea.workflowapi.controller;

import com.atenea.workflowapi.dto.ProcessInstanceHistoryPageResponse;
import com.atenea.workflowapi.dto.ProcessInstanceHistoryResponse;
import com.atenea.workflowapi.dto.ProcessInstanceResponse;
import com.atenea.workflowapi.dto.StartProcessRequest;
import com.atenea.workflowapi.service.ProcessInstanceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "Process instances",
        description = "Inicio y consulta de instancias."
)
@RestController
@RequestMapping("/api/process-instances")
public class ProcessInstanceController {

    private final ProcessInstanceService processInstanceService;

    public ProcessInstanceController(
            ProcessInstanceService processInstanceService
    ) {
        this.processInstanceService = processInstanceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProcessInstanceResponse start(
            @Valid @RequestBody StartProcessRequest request
    ) {
        return processInstanceService.start(request);
    }

    @GetMapping
    public ProcessInstanceHistoryPageResponse findHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return processInstanceService.findHistory(page, size);
    }

    @GetMapping("/{instanceId}")
    public ProcessInstanceHistoryResponse findById(
            @PathVariable String instanceId
    ) {
        return processInstanceService.findById(instanceId);
    }
}