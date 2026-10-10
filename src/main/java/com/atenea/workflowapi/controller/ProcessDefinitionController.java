package com.atenea.workflowapi.controller;

import com.atenea.workflowapi.dto.DeploymentResponse;
import com.atenea.workflowapi.dto.ProcessDefinitionResponse;
import com.atenea.workflowapi.service.ProcessDefinitionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Process definitions",
        description = "Publicación y consulta de definiciones BPMN."
)
@RestController
@RequestMapping("/api/process-definitions")
public class ProcessDefinitionController {

    private final ProcessDefinitionService processDefinitionService;

    public ProcessDefinitionController(
            ProcessDefinitionService processDefinitionService
    ) {
        this.processDefinitionService = processDefinitionService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProcessDefinitionResponse> findLatest() {
        return processDefinitionService.findLatest();
    }

    @PostMapping(
            value = "/deploy",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public DeploymentResponse deploy(@RequestBody String bpmnXml) {
        return processDefinitionService.deploy(bpmnXml);
    }
}