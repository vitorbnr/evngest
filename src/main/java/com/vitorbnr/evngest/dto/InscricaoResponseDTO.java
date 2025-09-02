package com.vitorbnr.evngest.dto;

import java.time.LocalDateTime;

public class InscricaoResponseDTO {

    private Long id;
    private UsuarioResponseDTO usuario;
    private EventoResponseDTO evento;
    private LocalDateTime dataInscricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioResponseDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDTO usuario) {
        this.usuario = usuario;
    }

    public EventoResponseDTO getEvento() {
        return evento;
    }

    public void setEvento(EventoResponseDTO evento) {
        this.evento = evento;
    }

    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDateTime dataInscricao) {
        this.dataInscricao = dataInscricao;
    }
}
