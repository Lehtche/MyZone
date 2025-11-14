package dev.JavaLovers.MyZone.dto;

// (Não precisa de anotações, é só uma classe Java pura)
public class FilmeDTO {
    private String nome;
    private String diretor;
    private int anoLancamento;
    // Campos da Avaliação
    private int nota;
    private String comentario;

    // Getters e Setters para todos os campos...
    // (O Spring precisa deles para ler o JSON)
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDiretor() { return diretor; }
    public void setDiretor(String diretor) { this.diretor = diretor; }
    public int getAnoLancamento() { return anoLancamento; }
    public void setAnoLancamento(int anoLancamento) { this.anoLancamento = anoLancamento; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}