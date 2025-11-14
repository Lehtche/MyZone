package dev.JavaLovers.MyZone.dto;

public class SerieDTO {
    private String nome;
    private String genero; // <-- Trocado de 'int temporadas'
    private int nota;
    private String comentario;

    // Getters e Setters...
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}