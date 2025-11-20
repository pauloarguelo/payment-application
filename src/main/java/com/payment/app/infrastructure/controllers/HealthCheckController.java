package com.payment.app.infrastructure.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health")
public class HealthCheckController {

    @Operation(
            summary = "Health Check Endpoint",
            description = "Returns a simple 'Hello World' message to indicate the service is running."
    )
    @GetMapping("/health")
    public String checkHealth() {
        return "Hello World";
    }
}
