package com.payment.app.infrastructure.controllers;

import com.payment.app.application.port.in.CreatePaymentUseCase;
import com.payment.app.application.dto.PaymentRequest;
import com.payment.app.application.dto.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Payments", description = "API for payment processing")
@RequestMapping("/api/v1")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final CreatePaymentUseCase useCase;

    public PaymentController(CreatePaymentUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/payments")
    @Operation(
            summary = "Create new payment",
            description = "Process a new payment request with idempotency support"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "paymentId": "123e4567-e89b-12d3-a456-426614174000",
                                                "firstName": "John",
                                                "lastName": "Doe",
                                                "status": "COMPLETED",
                                                "createdAt": "2025-11-21T10:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = com.payment.app.infrastructure.controllers.dto.ValidationErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "error": "VALIDATION_ERROR",
                                                "description": "One or more fields are invalid",
                                                "statusCode": 400,
                                                "fieldErrors": [
                                                    {
                                                        "field": "cardNumber",
                                                        "message": "Card number must be exactly 16 digits"
                                                    },
                                                    {
                                                        "field": "firstName",
                                                        "message": "First name is required"
                                                    }
                                                ],
                                                "timestamp": "2025-11-21T10:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Idempotency conflict - payment already processed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = com.payment.app.infrastructure.controllers.dto.ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "error": "IDEMPOTENCY_ERROR",
                                                "description": "A payment with this idempotency key has already been processed",
                                                "statusCode": 409,
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
    public ResponseEntity<PaymentResponse> createPayment(
            @Parameter(
                    description = "Unique key to ensure idempotency - prevents duplicate payments",
                    required = true,
                    example = "payment_123456789"
            )
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey,

            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payment data to be processed",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Valid payment",
                                            summary = "Complete payment with all required fields",
                                            value = """
                                                    {
                                                        "firstName": "John",
                                                        "lastName": "Doe",
                                                        "zipCode": "2000",
                                                        "cardNumber": "1234567812345678"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Another customer",
                                            summary = "Payment for different customer",
                                            value = """
                                                    {
                                                        "firstName": "Jane",
                                                        "lastName": "Smith",
                                                        "zipCode": "3000",
                                                        "cardNumber": "9876543210987654"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            PaymentRequest request
    ) {
        logger.info("Received request to create payment - IdempotencyKey: {}", idempotencyKey);

        try {
            PaymentResponse response = useCase.createPayment(request, idempotencyKey);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            logger.error("Failed to create webhook: {}", e.getMessage(), e);
            throw e;
        }

    }
}


