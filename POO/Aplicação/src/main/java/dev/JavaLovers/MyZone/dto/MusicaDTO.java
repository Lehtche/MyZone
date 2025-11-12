package dev.JavaLovers.MyZone.dto;

public class MusicaDTO {
    private String nome;
    private String artista;
    // (Pode adicionar os outros campos 'album' e 'dataEstreia' aqui também)
    private int nota;
    private String comentario;

    // Getters e Setters...
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}