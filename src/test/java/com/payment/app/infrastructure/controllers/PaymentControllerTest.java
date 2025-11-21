package com.payment.app.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.app.application.dto.PaymentRequest;
import com.payment.app.application.dto.PaymentResponse;
import com.payment.app.application.port.in.CreatePaymentUseCase;
import com.payment.app.domain.type.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("PaymentController Tests")
class PaymentControllerTest {


    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private CreatePaymentUseCase createPaymentUseCase;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        createPaymentUseCase = mock(CreatePaymentUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                new PaymentController(createPaymentUseCase)
        ).build();
    }

    @Test
    @DisplayName("Should successfully create payment with valid request")
    void testCreatePaymentSuccess() throws Exception {

        PaymentRequest paymentRequest = new PaymentRequest(
                "John",
                "Doe",
                "2000",
                "1234567812345678"
        );

        UUID paymentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        PaymentResponse expectedResponse = new PaymentResponse(
                paymentId,
                "John",
                "Doe",
                PaymentStatus.CREATED,
                createdAt
        );

        String idempotencyKey = "idempotency-key-123";

        when(createPaymentUseCase.createPayment(any(PaymentRequest.class), eq(idempotencyKey)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/payments")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(paymentId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @DisplayName("Should return 200 OK with correct response structure")
    void testCreatePaymentResponseStructure() throws Exception {

        PaymentRequest paymentRequest = new PaymentRequest(
                "Jane",
                "Smith",
                "2001",
                "9876543210123456"
        );

        UUID paymentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        PaymentResponse expectedResponse = new PaymentResponse(
                paymentId,
                "Jane",
                "Smith",
                PaymentStatus.CREATED,
                createdAt
        );

        String idempotencyKey = "unique-key-456";

        when(createPaymentUseCase.createPayment(any(PaymentRequest.class), eq(idempotencyKey)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/payments")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.paymentId").exists())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }
}

