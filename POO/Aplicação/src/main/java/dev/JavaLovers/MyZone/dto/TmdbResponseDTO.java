package dev.JavaLovers.MyZone.dto;

public class TmdbResponseDTO {
    private String nome; // <-- CAMPO ADICIONADO
    private String posterUrl;
    private String sinopse;
    private int anoLancamento;
    private String genero;
    private String diretor;

    // <-- CONSTRUTOR ATUALIZADO (com 6 argumentos) -->
    public TmdbResponseDTO(String nome, String posterUrl, String sinopse, int anoLancamento, String genero, String diretor) {
        this.nome = nome; // <-- CAMPO ADICIONADO
        this.posterUrl = posterUrl;
        this.sinopse = sinopse;
        this.anoLancamento = anoLancamento;
        this.genero = genero;
        this.diretor = diretor;
    }

    // Getters
    public String getNome() { return nome; } // <-- GETTER ADICIONADO
    public String getPosterUrl() { return posterUrl; }
    public String getSinopse() { return sinopse; }
    public int getAnoLancamento() { return anoLancamento; }
    public String getGenero() { return genero; }
    public String getDiretor() { return diretor; }
}