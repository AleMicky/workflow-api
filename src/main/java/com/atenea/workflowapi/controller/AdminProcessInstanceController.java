package com.atenea.workflowapi.controller;

import com.atenea.workflowapi.dto.CancelProcessInstanceRequest;
import com.atenea.workflowapi.dto.ProcessInstanceActionResponse;
import com.atenea.workflowapi.service.ProcessInstanceAdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Administration",
        description = "Operaciones administrativas sobre procesos."
)
@RestController
@RequestMapping("/api/admin/process-instances")
public class AdminProcessInstanceController {

    private final ProcessInstanceAdminService adminService;

    public AdminProcessInstanceController(
            ProcessInstanceAdminService adminService
    ) {
        this.adminService = adminService;
    }

    @PostMapping("/{instanceId}/suspend")
    public ProcessInstanceActionResponse suspend(
            @PathVariable String instanceId
    ) {
        return adminService.suspend(instanceId);
    }

    @PostMapping("/{instanceId}/activate")
    public ProcessInstanceActionResponse activate(
            @PathVariable String instanceId
    ) {
        return adminService.activate(instanceId);
    }

    @PostMapping("/{instanceId}/cancel")
    public ProcessInstanceActionResponse cancel(
            @PathVariable String instanceId,
            @Valid @RequestBody CancelProcessInstanceRequest request
    ) {
        return adminService.cancel(
                instanceId,
                request.reason()
        );
    }
}