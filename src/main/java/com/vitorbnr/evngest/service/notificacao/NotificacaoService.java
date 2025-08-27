package com.vitorbnr.evngest.service.notificacao;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String QUEUE_NAME = "fila.notificacoes";

    public void enviarNotificacao(String mensagem) {
        amqpTemplate.convertAndSend(QUEUE_NAME, mensagem);
        System.out.println("Mensagem enviada para a fila: " + mensagem);
    }
}
