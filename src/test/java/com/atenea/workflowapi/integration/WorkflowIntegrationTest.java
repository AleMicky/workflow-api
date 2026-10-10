package com.atenea.workflowapi.integration;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class WorkflowIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer(
                    DockerImageName.parse("postgres:17-alpine")
            );

    private static final String BPMN_XML = """
            <?xml version="1.0" encoding="UTF-8"?>
            <definitions
                xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
                xmlns:flowable="http://flowable.org/bpmn"
                targetNamespace="urn:test:workflow">

                <process
                    id="integrationTestProcess"
                    name="Integration test process"
                    isExecutable="true">

                    <startEvent id="start" />

                    <userTask
                        id="approval"
                        name="Approve request"
                        flowable:candidateUsers="${candidateUsers}" />

                    <endEvent id="end" />

                    <sequenceFlow
                        id="flow1"
                        sourceRef="start"
                        targetRef="approval" />

                    <sequenceFlow
                        id="flow2"
                        sourceRef="approval"
                        targetRef="end" />
                </process>
            </definitions>
            """;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Test
    void deploysStartsClaimsAndCompletesProcess() {
        Deployment deployment = repositoryService
                .createDeployment()
                .name("Integration test")
                .addString("integration-test.bpmn20.xml", BPMN_XML)
                .deploy();

        ProcessDefinition definition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();

        assertThat(definition).isNotNull();
        assertThat(definition.getKey())
                .isEqualTo("integrationTestProcess");

        ProcessInstance instance =
                runtimeService.startProcessInstanceByKey(
                        "integrationTestProcess",
                        "TEST-001",
                        Map.of(
                                "candidateUsers",
                                List.of("micky", "juan")
                        )
                );

        assertThat(instance).isNotNull();
        assertThat(instance.getBusinessKey())
                .isEqualTo("TEST-001");

        Task candidateTask = taskService
                .createTaskQuery()
                .taskCandidateUser("micky")
                .singleResult();

        assertThat(candidateTask).isNotNull();
        assertThat(candidateTask.getName())
                .isEqualTo("Approve request");
        assertThat(candidateTask.getAssignee()).isNull();

        taskService.claim(candidateTask.getId(), "micky");

        Task claimedTask = taskService
                .createTaskQuery()
                .taskId(candidateTask.getId())
                .singleResult();

        assertThat(claimedTask.getAssignee())
                .isEqualTo("micky");

        taskService.complete(
                claimedTask.getId(),
                Map.of("decision", "approved")
        );

        ProcessInstance activeInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(instance.getId())
                .singleResult();

        assertThat(activeInstance).isNull();

        HistoricProcessInstance history = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(instance.getId())
                .singleResult();

        assertThat(history).isNotNull();
        assertThat(history.getEndTime()).isNotNull();
        assertThat(history.getBusinessKey())
                .isEqualTo("TEST-001");
    }
}