package com.vitorbnr.evngest.service.notificacao;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoListener {

    private static final String QUEUE_NAME = "fila.notificacoes";

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = QUEUE_NAME)
    public void processarMensagem(String mensagem) {
        System.out.println("-----------------------------------");
        System.out.println("Mensagem do RabbitMQ recebida: " + mensagem);
        System.out.println("A enviar para o EmailService...");
        System.out.println("-----------------------------------");

        String emailDestino = "vitorevngest5@gmail.com";
        String assunto = "Nova Notificação do EvnGest";

        emailService.enviarEmail(emailDestino, assunto, mensagem);
    }
}
