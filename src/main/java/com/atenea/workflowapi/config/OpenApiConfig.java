package com.atenea.workflowapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Workflow API",
                version = "1.0.0",
                description = """
                        Microservicio para publicar y ejecutar procesos BPMN
                        mediante Flowable.
                        """,
                contact = @Contact(
                        name = "Atenea"
                )
        )
)
public class OpenApiConfig {
}