package com.vitorbnr.evngest.service.notificacao;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoListener {

    private static final String QUEUE_NAME = "fila.notificacoes";

    @RabbitListener(queues = QUEUE_NAME)
    public void processarMensagem(String mensagem) {
        System.out.println("-----------------------------------");
        System.out.println("Mensagem recebida do RabbitMQ:");
        System.out.println(mensagem);
        System.out.println("-----------------------------------");
    }
}
