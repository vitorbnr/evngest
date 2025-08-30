package com.vitorbnr.evngest.service.notificacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmail(String para, String assunto, String texto) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom("no-reply@evngest.com");
            mensagem.setTo(para);
            mensagem.setSubject(assunto);
            mensagem.setText(texto);
            mailSender.send(mensagem);
            System.out.println("E-mail de notificação capturado pelo Mailtrap com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail para o Mailtrap: " + e.getMessage());
        }
    }
}
