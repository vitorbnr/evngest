package com.vitorbnr.evngest.dto;

import java.time.LocalDateTime;

public class EventoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private LocalDateTime dataEvento;
    private String localizacao;
    private UsuarioResponseDTO criador;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDateTime dataEvento) {
        this.dataEvento = dataEvento;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public UsuarioResponseDTO getCriador() {
        return criador;
    }

    public void setCriador(UsuarioResponseDTO criador) {
        this.criador = criador;
    }
}
