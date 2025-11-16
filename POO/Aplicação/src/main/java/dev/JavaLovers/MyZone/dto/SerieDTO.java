package dev.JavaLovers.MyZone.dto;

public class SerieDTO {
    private String nome;
    private String genero; // <-- Trocado de 'int temporadas'
    private int nota;
    private String comentario;

    // --- CAMPOS ADICIONADOS ---
    private String posterUrl;
    private String sinopse;

    // Getters e Setters...
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    // --- GETTERS E SETTERS ADICIONADOS ---
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }
}