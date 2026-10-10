package com.atenea.workflowapi.service;

import com.atenea.workflowapi.dto.CompleteTaskRequest;
import com.atenea.workflowapi.dto.TaskCompletionResponse;
import com.atenea.workflowapi.dto.TaskResponse;
import com.atenea.workflowapi.exception.ResourceNotFoundException;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WorkflowTaskService {

    private final TaskService taskService;
    private final RuntimeService runtimeService;

    public WorkflowTaskService(
            TaskService taskService,
            RuntimeService runtimeService
    ) {
        this.taskService = taskService;
        this.runtimeService = runtimeService;
    }

    public List<TaskResponse> findAssignedTasks(String assignee) {
        if (assignee == null || assignee.isBlank()) {
            throw new IllegalArgumentException(
                    "El responsable es obligatorio."
            );
        }

        return taskService
                .createTaskQuery()
                .taskAssignee(assignee)
                .active()
                .orderByTaskCreateTime()
                .desc()
                .list()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskCompletionResponse complete(
            String taskId,
            CompleteTaskRequest request
    ) {
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new IllegalArgumentException(
                    "No existe una tarea activa con id: " + taskId
            );
        }

        String processInstanceId = task.getProcessInstanceId();

        Map<String, Object> variables =
                request == null || request.variables() == null
                        ? Map.of()
                        : request.variables();

        taskService.complete(taskId, variables);

        boolean processEnded = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult() == null;

        return new TaskCompletionResponse(
                taskId,
                processInstanceId,
                true,
                processEnded
        );
    }

    public List<TaskResponse> findCandidateTasks(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(
                    "El usuario es obligatorio."
            );
        }

        return taskService
                .createTaskQuery()
                .taskCandidateUser(userId)
                .active()
                .orderByTaskCreateTime()
                .desc()
                .list()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse claim(String taskId, String userId) {
        findTask(taskId);

        taskService.claim(taskId, userId);

        return toResponse(findTask(taskId));
    }

    public TaskResponse unclaim(String taskId) {
        findTask(taskId);

        taskService.unclaim(taskId);

        return toResponse(findTask(taskId));
    }

    private Task findTask(String taskId) {
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new ResourceNotFoundException(
                    "No existe una tarea activa con id: " + taskId
            );
        }

        return task;
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getAssignee(),
                task.getTaskDefinitionKey(),
                task.getProcessInstanceId(),
                task.getProcessDefinitionId(),
                task.getCreateTime() == null
                        ? null
                        : task.getCreateTime().toInstant()
        );
    }
}