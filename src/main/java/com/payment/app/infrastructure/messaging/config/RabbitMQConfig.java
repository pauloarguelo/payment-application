package com.payment.app.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // --- 1. Nomes Comuns (DLQ) ---
    public static final String DLQ_QUEUE_NAME = "webhooks.dlq";
    public static final String DLQ_EXCHANGE_NAME = "webhooks.dlq.exchange";

    // --- 2. Fila Principal (Orquestração / Fanout) ---
    public static final String MAIN_QUEUE_NAME = "webhooks.main";
    public static final String MAIN_EXCHANGE_NAME = "webhooks.main.exchange";

    // --- 3. Fila de Entrega (Execução / Retry de Chamada HTTP) ---
    public static final String DELIVERY_QUEUE_NAME = "webhooks.delivery";
    public static final String DELIVERY_EXCHANGE_NAME = "webhooks.delivery.exchange";


    // --- CONFIGURAÇÃO DLQ (Compartilhada) ---

    @Bean
    public DirectExchange dlqExchange() {
        // Exchange para onde mensagens mortas são enviadas
        return new DirectExchange(DLQ_EXCHANGE_NAME);
    }

    @Bean
    public Queue dlqQueue() {
        // Fila que recebe as mensagens mortas de TODAS as outras filas
        return new Queue(DLQ_QUEUE_NAME);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue()).to(dlqExchange()).with(DLQ_QUEUE_NAME);
    }


    // --- CONFIGURAÇÃO 1: FILA PRINCIPAL (ORQUESTRAÇÃO) ---

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(MAIN_EXCHANGE_NAME);
    }

    @Bean
    public Queue mainQueue() {
        // Fila que recebe o evento de PaymentCreated do Use Case Síncrono.
        return new Queue(MAIN_QUEUE_NAME, true, false, false,
                // Configura a DLQ para esta fila
                Map.of(
                        "x-dead-letter-exchange", DLQ_EXCHANGE_NAME,
                        "x-dead-letter-routing-key", DLQ_QUEUE_NAME
                )
        );
    }

    @Bean
    public Binding mainBinding() {
        return BindingBuilder.bind(mainQueue()).to(mainExchange()).with(MAIN_QUEUE_NAME);
    }


    // --- CONFIGURAÇÃO 2: FILA DE ENTREGA (EXECUÇÃO) ---

    @Bean
    public DirectExchange deliveryExchange() {
        return new DirectExchange(DELIVERY_EXCHANGE_NAME);
    }

    @Bean
    public Queue deliveryQueue() {
        // Fila que recebe a tarefa individual (paymentId, webhookId) para TENTAR a chamada HTTP.
        // Se a chamada HTTP falhar repetidamente, ela vai para a mesma DLQ.
        return new Queue(DELIVERY_QUEUE_NAME, true, false, false,
                // Configura a DLQ para esta fila
                Map.of(
                        "x-dead-letter-exchange", DLQ_EXCHANGE_NAME,
                        "x-dead-letter-routing-key", DLQ_QUEUE_NAME
                )
        );
    }

    @Bean
    public Binding deliveryBinding() {
        return BindingBuilder.bind(deliveryQueue()).to(deliveryExchange()).with(DELIVERY_QUEUE_NAME);
    }
}