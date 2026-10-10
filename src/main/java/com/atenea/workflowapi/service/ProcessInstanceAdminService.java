package com.atenea.workflowapi.service;

import com.atenea.workflowapi.dto.ProcessInstanceActionResponse;
import com.atenea.workflowapi.exception.ConflictException;
import com.atenea.workflowapi.exception.ResourceNotFoundException;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

@Service
public class ProcessInstanceAdminService {

    private final RuntimeService runtimeService;

    public ProcessInstanceAdminService(
            RuntimeService runtimeService
    ) {
        this.runtimeService = runtimeService;
    }

    public ProcessInstanceActionResponse suspend(String instanceId) {
        ProcessInstance instance = findActiveInstance(instanceId);

        if (instance.isSuspended()) {
            throw new ConflictException(
                    "La instancia ya se encuentra suspendida."
            );
        }

        runtimeService.suspendProcessInstanceById(instanceId);

        return new ProcessInstanceActionResponse(
                instanceId,
                "SUSPENDED",
                "Instancia suspendida correctamente."
        );
    }

    public ProcessInstanceActionResponse activate(String instanceId) {
        ProcessInstance instance = findActiveInstance(instanceId);

        if (!instance.isSuspended()) {
            throw new ConflictException(
                    "La instancia no está suspendida."
            );
        }

        runtimeService.activateProcessInstanceById(instanceId);

        return new ProcessInstanceActionResponse(
                instanceId,
                "RUNNING",
                "Instancia reactivada correctamente."
        );
    }

    public ProcessInstanceActionResponse cancel(
            String instanceId,
            String reason
    ) {
        findActiveInstance(instanceId);

        runtimeService.deleteProcessInstance(
                instanceId,
                reason
        );

        return new ProcessInstanceActionResponse(
                instanceId,
                "CANCELLED",
                "Instancia cancelada correctamente."
        );
    }

    private ProcessInstance findActiveInstance(String instanceId) {
        ProcessInstance instance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();

        if (instance == null) {
            throw new ResourceNotFoundException(
                    "No existe una instancia activa con id: " + instanceId
            );
        }

        return instance;
    }
}