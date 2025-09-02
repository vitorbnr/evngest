package com.vitorbnr.evngest.dto;

public class AvaliacaoResponseDTO {
    private Long id;
    private Integer nota;
    private UsuarioResponseDTO usuario;

    public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }
    public UsuarioResponseDTO getUsuario() { return usuario; }
    public void setUsuario(UsuarioResponseDTO usuario) { this.usuario = usuario; }
}
