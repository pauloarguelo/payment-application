package com.payment.app.infrastructure.http;

import com.payment.app.application.dto.WebhookResponse;
import com.payment.app.application.port.out.WebhookClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class WebhookClientPortImpl implements WebhookClientPort {

    private static final Logger logger = LoggerFactory.getLogger(WebhookClientPortImpl.class);
    private final RestClient restClient;

    public WebhookClientPortImpl(RestClient webhookRestClient) {
        this.restClient = webhookRestClient;
    }

    @Override
    public WebhookResponse postEvent(String url, String payloadJson) {
        try {
            logger.debug("Attempting POST to {}", url);

            ResponseEntity<String> entity = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payloadJson)
                    .retrieve()
                    .toEntity(String.class);

            return new WebhookResponse(entity.getStatusCode().value(), entity.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.warn("HTTP Error from webhook {}: {} - {}", url, e.getStatusCode(), e.getResponseBodyAsString());
            return new WebhookResponse(e.getStatusCode().value(), e.getResponseBodyAsString());

        } catch (ResourceAccessException e) {
            logger.error("Network failure calling webhook {}: {}", url, e.getMessage());
            throw new RuntimeException("Network failure: " + e.getMessage(), e);

        } catch (Exception e) {
            logger.error("Unexpected error calling webhook {}: {}", url, e.getMessage());
            throw new RuntimeException("Unexpected error", e);
        }
    }
}