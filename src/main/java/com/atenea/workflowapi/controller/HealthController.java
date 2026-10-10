package com.atenea.workflowapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.engine.RepositoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Health", description = "Estado del microservicio.")
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final RepositoryService repositoryService;

    public HealthController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping
    public Map<String, Object> health() {
        long processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .count();

        return Map.of(
                "status", "UP",
                "engine", "Flowable",
                "processDefinitions", processDefinitions
        );
    }
}