package com.payment.app.infrastructure.httpClient;

import com.payment.app.application.dto.WebhookClientResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebhookClientPortImpl Tests")
class WebhookClientPortImplTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private WebhookClientPortImpl webhookClient;

    private String url;
    private String payloadJson;

    @BeforeEach
    void setUp() {
        webhookClient = new WebhookClientPortImpl(restClient);
        url = "https://example.com/webhook";
        payloadJson = "{\"event\":\"payment.created\",\"data\":{}}";
    }

    @Test
    @DisplayName("Should return successful response when webhook returns 200")
    void postEvent_shouldReturnSuccessResponse() {
        ResponseEntity<String> responseEntity = ResponseEntity.ok("Success");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(payloadJson)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenReturn(responseEntity);

        WebhookClientResponse response = webhookClient.postEvent(url, payloadJson);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("Success");
        assertThat(response.isSuccess()).isTrue();
        verify(restClient).post();
    }

    @Test
    @DisplayName("Should return error response when webhook returns 400")
    void postEvent_shouldReturnErrorResponse_when400() {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                "Invalid payload".getBytes(),
                null
        );

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(payloadJson)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenThrow(exception);

        WebhookClientResponse response = webhookClient.postEvent(url, payloadJson);

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.responseBody()).isEqualTo("Invalid payload");
        assertThat(response.isPermanentError()).isTrue();
        verify(restClient).post();
    }

    @Test
    @DisplayName("Should return error response when webhook returns 500")
    void postEvent_shouldReturnErrorResponse_when500() {
        HttpServerErrorException exception = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                null,
                "Server error".getBytes(),
                null
        );

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(payloadJson)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenThrow(exception);

        WebhookClientResponse response = webhookClient.postEvent(url, payloadJson);

        assertThat(response.statusCode()).isEqualTo(500);
        assertThat(response.responseBody()).isEqualTo("Server error");
        assertThat(response.isSuccess()).isFalse();
        verify(restClient).post();
    }

    @Test
    @DisplayName("Should throw exception when network failure occurs")
    void postEvent_shouldThrowException_whenNetworkFailure() {
        ResourceAccessException exception = new ResourceAccessException("Connection timeout");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(payloadJson)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenThrow(exception);

        assertThatThrownBy(() -> webhookClient.postEvent(url, payloadJson))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Network failure")
                .hasCauseInstanceOf(ResourceAccessException.class);

        verify(restClient).post();
    }

    @Test
    @DisplayName("Should throw exception when unexpected error occurs")
    void postEvent_shouldThrowException_whenUnexpectedError() {
        RuntimeException exception = new RuntimeException("Unexpected error");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(payloadJson)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class)).thenThrow(exception);

        assertThatThrownBy(() -> webhookClient.postEvent(url, payloadJson))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unexpected error")
                .hasCause(exception);

        verify(restClient).post();
    }
}

