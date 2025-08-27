package com.vitorbnr.evngest.config.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "fila.notificacoes";

    @Bean
    public Queue filaNotificacoes() {
        return new Queue(QUEUE_NAME, true);
    }
}
