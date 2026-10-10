package com.atenea.workflowapi.service;

import com.atenea.workflowapi.dto.ProcessInstanceHistoryPageResponse;
import com.atenea.workflowapi.dto.ProcessInstanceHistoryResponse;
import com.atenea.workflowapi.dto.ProcessInstanceResponse;
import com.atenea.workflowapi.dto.StartProcessRequest;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProcessInstanceService {

    private final RuntimeService runtimeService;
    private final HistoryService historyService;

    public ProcessInstanceService(
            RuntimeService runtimeService,
            HistoryService historyService
    ) {
        this.runtimeService = runtimeService;
        this.historyService = historyService;
    }

    public ProcessInstanceResponse start(StartProcessRequest request) {
        Map<String, Object> variables = request.variables() == null
                ? Map.of()
                : request.variables();

        ProcessInstance instance =
                runtimeService.startProcessInstanceByKey(
                        request.processKey(),
                        request.businessKey(),
                        variables
                );

        return new ProcessInstanceResponse(
                instance.getId(),
                instance.getProcessDefinitionId(),
                instance.getBusinessKey(),
                instance.isEnded(),
                instance.isSuspended()
        );
    }

    public ProcessInstanceHistoryPageResponse findHistory(
            int page,
            int size
    ) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "La página no puede ser negativa."
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "El tamaño debe estar entre 1 y 100."
            );
        }

        HistoricProcessInstanceQuery query = historyService
                .createHistoricProcessInstanceQuery();

        long total = query.count();

        List<ProcessInstanceHistoryResponse> items = query
                .orderByProcessInstanceStartTime()
                .desc()
                .listPage(page * size, size)
                .stream()
                .map(this::toHistoryResponse)
                .toList();

        return new ProcessInstanceHistoryPageResponse(
                items,
                total,
                page,
                size
        );
    }

    public ProcessInstanceHistoryResponse findById(String instanceId) {
        HistoricProcessInstance instance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();

        if (instance == null) {
            throw new IllegalArgumentException(
                    "No existe la instancia: " + instanceId
            );
        }

        return toHistoryResponse(instance);
    }

    private ProcessInstanceHistoryResponse toHistoryResponse(
            HistoricProcessInstance instance
    ) {
        String status;

        if (instance.getDeleteReason() != null) {
            status = "CANCELLED";
        } else if (instance.getEndTime() != null) {
            status = "COMPLETED";
        } else {
            status = "RUNNING";
        }

        return new ProcessInstanceHistoryResponse(
                instance.getId(),
                instance.getProcessDefinitionId(),
                instance.getProcessDefinitionKey(),
                instance.getBusinessKey(),
                status,
                instance.getStartTime() == null
                        ? null
                        : instance.getStartTime().toInstant(),
                instance.getEndTime() == null
                        ? null
                        : instance.getEndTime().toInstant(),
                instance.getDurationInMillis(),
                instance.getDeleteReason()
        );
    }
}