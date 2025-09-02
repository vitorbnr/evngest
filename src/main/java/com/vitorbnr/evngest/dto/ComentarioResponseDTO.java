package com.vitorbnr.evngest.dto;

import java.time.LocalDateTime;

public class ComentarioResponseDTO {
    private Long id;
    private String texto;
    private UsuarioResponseDTO usuario;
    private LocalDateTime dataComentario;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public UsuarioResponseDTO getUsuario() { return usuario; }
    public void setUsuario(UsuarioResponseDTO usuario) { this.usuario = usuario; }
    public LocalDateTime getDataComentario() { return dataComentario; }
    public void setDataComentario(LocalDateTime dataComentario) { this.dataComentario = dataComentario; }
}
