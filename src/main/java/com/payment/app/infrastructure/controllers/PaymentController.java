package com.payment.app.infrastructure.controllers;

import com.payment.app.application.port.in.CreatePaymentUseCase;
import com.payment.app.application.dto.PaymentRequest;
import com.payment.app.application.dto.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Payment")
@RequestMapping("/api/v1")
public class PaymentController {

    private final CreatePaymentUseCase useCase;

    public PaymentController( CreatePaymentUseCase useCase) {
        this.useCase = useCase;
    }


    @PostMapping("/payments")
    @Operation(summary = "Process a new payment request")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey,
            @Valid @RequestBody PaymentRequest request
    ) {
        PaymentResponse response = useCase.createPayment(request, idempotencyKey);
        return ResponseEntity.ok(response);

    }
}
