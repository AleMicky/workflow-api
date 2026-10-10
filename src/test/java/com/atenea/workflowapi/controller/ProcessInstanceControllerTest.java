package com.atenea.workflowapi.controller;

import com.atenea.workflowapi.dto.ProcessInstanceResponse;
import com.atenea.workflowapi.exception.GlobalExceptionHandler;
import com.atenea.workflowapi.exception.ResourceNotFoundException;
import com.atenea.workflowapi.service.ProcessInstanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcessInstanceController.class)
@Import(GlobalExceptionHandler.class)
class ProcessInstanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessInstanceService processInstanceService;

    @Test
    void startsProcessInstance() throws Exception {
        when(processInstanceService.start(any()))
                .thenReturn(new ProcessInstanceResponse(
                        "instance-1",
                        "purchaseProcess:1:definition",
                        "COMPRA-001",
                        false,
                        false
                ));

        mockMvc.perform(post("/api/process-instances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "processKey": "purchaseProcess",
                                  "businessKey": "COMPRA-001",
                                  "variables": {
                                    "candidateUsers": ["micky"]
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value("instance-1"))
                .andExpect(jsonPath("$.businessKey")
                        .value("COMPRA-001"))
                .andExpect(jsonPath("$.ended")
                        .value(false));
    }

    @Test
    void rejectsRequestWithoutProcessKey() throws Exception {
        mockMvc.perform(post("/api/process-instances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "businessKey": "COMPRA-001",
                                  "variables": {}
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath(
                        "$.validationErrors.processKey"
                ).value(
                        "La clave del proceso es obligatoria."
                ));
    }

    @Test
    void returnsNotFoundForUnknownInstance() throws Exception {
        when(processInstanceService.findById("missing"))
                .thenThrow(new ResourceNotFoundException(
                        "No existe la instancia: missing"
                ));

        mockMvc.perform(get(
                        "/api/process-instances/missing"
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value("No existe la instancia: missing"));
    }
}