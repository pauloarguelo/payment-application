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
@Tag(name = "Webhooks", description = "API para gerenciamento de webhooks")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    private final CreateWebhookUseCase createWebhookUseCase;

    public WebhookController(CreateWebhookUseCase createWebhookUseCase) {
        this.createWebhookUseCase = createWebhookUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Criar novo webhook",
            description = "Registra um novo webhook para receber notificações de eventos de pagamento"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Webhook criado com sucesso",
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
                    description = "Requisição inválida - dados incorretos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "error": "Bad Request",
                                        "message": "Invalid event type: INVALID_TYPE. Valid values are: PAYMENT_CREATED, PAYMENT_FAILED, PAYMENT_REFUNDED"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "error": "Internal Server Error",
                                        "message": "Erro ao criar webhook"
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<WebhookResponse> createWebhook(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do webhook a ser criado",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebhookRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Webhook básico",
                                            summary = "Webhook sem secret key",
                                            value = """
                                            {
                                                "endpointUrl": "https://api.example.com/webhooks/payment",
                                                "eventType": "PAYMENT_CREATED"
                                            }
                                            """
                                    ),
                            }
                    )
            )
            WebhookRequest request
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

