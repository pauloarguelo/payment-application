package com.payment.app.infrastructure.controllers;

import com.payment.app.application.dto.WebhookRequest;
import com.payment.app.application.dto.WebhookResponse;
import com.payment.app.application.port.in.CreateWebhookUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@Tag(name = "Webhooks", description = "API for webhook management")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    private final CreateWebhookUseCase createWebhookUseCase;

    public WebhookController(CreateWebhookUseCase createWebhookUseCase) {
        this.createWebhookUseCase = createWebhookUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Create new webhook",
            description = "Register a new webhook to receive payment event notifications"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Webhook created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebhookResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "id": "123e4567-e89b-12d3-a456-426614174000",
                                        "endpointUrl": "https://api.example.com/webhooks/payment",
                                        "eventType": "PAYMENT_CREATED",
                                        "status": "ACTIVE",
                                        "createdAt": "2025-11-21T10:30:00",
                                        "updatedAt": "2025-11-21T10:30:00"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - incorrect data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "error": "INVALID_ENUM_VALUE",
                                        "description": "Invalid event type. Allowed values are: PAYMENT_CREATED, PAYMENT_FAILED, PAYMENT_REFUNDED.",
                                        "statusCode": 400,
                                        "timestamp": "2025-11-21T10:30:00"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = com.payment.app.infrastructure.controllers.dto.ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "error": "INTERNAL_SERVER_ERROR",
                                        "description": "An unexpected error occurred.",
                                        "statusCode": 500,
                                        "timestamp": "2025-11-21T10:30:00"
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<WebhookResponse> createWebhook(
            @Valid @RequestBody WebhookRequest request
    ) {
        logger.info("Received request to create webhook - EndpointUrl: {}", request.endpointUrl());

        try {
            WebhookResponse response = createWebhookUseCase.createWebhook(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            logger.error("Failed to create webhook: {}", e.getMessage(), e);
            throw e;
        }
    }
}

