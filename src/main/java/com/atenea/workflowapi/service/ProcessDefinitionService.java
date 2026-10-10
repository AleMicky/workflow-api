package com.atenea.workflowapi.service;

import com.atenea.workflowapi.dto.DeploymentResponse;
import com.atenea.workflowapi.dto.ProcessDefinitionResponse;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessDefinitionService {

    private final RepositoryService repositoryService;

    public ProcessDefinitionService(
            RepositoryService repositoryService
    ) {
        this.repositoryService = repositoryService;
    }

    public DeploymentResponse deploy(String bpmnXml) {
        if (bpmnXml == null || bpmnXml.isBlank()) {
            throw new IllegalArgumentException(
                    "El contenido BPMN no puede estar vacío."
            );
        }

        Deployment deployment = repositoryService
                .createDeployment()
                .name("Publicado desde Miky Flow")
                .addString("process.bpmn20.xml", bpmnXml)
                .deploy();

        ProcessDefinition definition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();

        if (definition == null) {
            throw new IllegalStateException(
                    "Flowable no encontró una definición BPMN."
            );
        }

        return new DeploymentResponse(
                deployment.getId(),
                definition.getId(),
                definition.getKey(),
                definition.getName(),
                definition.getVersion()
        );
    }

    public List<ProcessDefinitionResponse> findLatest() {
        return repositoryService
                .createProcessDefinitionQuery()
                .latestVersion()
                .list()
                .stream()
                .map(definition -> new ProcessDefinitionResponse(
                        definition.getId(),
                        definition.getKey(),
                        definition.getName(),
                        definition.getVersion(),
                        definition.getDeploymentId(),
                        definition.isSuspended()
                ))
                .toList();
    }
}