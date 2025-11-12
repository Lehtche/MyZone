package dev.JavaLovers.MyZone.dto;

public class LivroDTO {
    private String nome;
    private String autor;
    // (Pode adicionar 'paginas' aqui)
    private int nota;
    private String comentario;
    
    // Getters e Setters...
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}