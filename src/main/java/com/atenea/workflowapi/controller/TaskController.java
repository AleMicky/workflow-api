package com.atenea.workflowapi.controller;

import com.atenea.workflowapi.dto.CompleteTaskRequest;
import com.atenea.workflowapi.dto.TaskClaimRequest;
import com.atenea.workflowapi.dto.TaskCompletionResponse;
import com.atenea.workflowapi.dto.TaskResponse;
import com.atenea.workflowapi.service.WorkflowTaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Tasks",
        description = "Consulta, reclamación y finalización de tareas."
)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final WorkflowTaskService workflowTaskService;

    public TaskController(
            WorkflowTaskService workflowTaskService
    ) {
        this.workflowTaskService = workflowTaskService;
    }

    @GetMapping
    public List<TaskResponse> findAssignedTasks(
            @RequestParam String assignee
    ) {
        return workflowTaskService.findAssignedTasks(assignee);
    }

    @PostMapping("/{taskId}/complete")
    public TaskCompletionResponse complete(
            @PathVariable String taskId,
            @RequestBody(required = false) CompleteTaskRequest request
    ) {
        return workflowTaskService.complete(taskId, request);
    }

    @GetMapping("/candidate")
    public List<TaskResponse> findCandidateTasks(
            @RequestParam String userId
    ) {
        return workflowTaskService.findCandidateTasks(userId);
    }

    @PostMapping("/{taskId}/claim")
    public TaskResponse claim(
            @PathVariable String taskId,
            @Valid @RequestBody TaskClaimRequest request
    ) {
        return workflowTaskService.claim(
                taskId,
                request.userId()
        );
    }

    @PostMapping("/{taskId}/unclaim")
    public TaskResponse unclaim(
            @PathVariable String taskId
    ) {
        return workflowTaskService.unclaim(taskId);
    }
}