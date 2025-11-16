package dev.JavaLovers.MyZone.dto;

// DTO simples para receber dados de uma nova avaliação
public class AvaliacaoDTO {
    private Long midiaId;
    private int nota;
    private String comentario;

    // Getters e Setters
    public Long getMidiaId() { return midiaId; }
    public void setMidiaId(Long midiaId) { this.midiaId = midiaId; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}