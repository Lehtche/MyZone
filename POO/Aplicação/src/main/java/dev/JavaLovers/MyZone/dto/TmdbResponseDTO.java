package dev.JavaLovers.MyZone.dto;

public class TmdbResponseDTO {
    private String posterUrl;
    private String sinopse;
    private int anoLancamento;
    private String genero;
    private String diretor;

    // <-- CONSTRUTOR ATUALIZADO (com 5 argumentos) -->
    public TmdbResponseDTO(String posterUrl, String sinopse, int anoLancamento, String genero, String diretor) {
        this.posterUrl = posterUrl;
        this.sinopse = sinopse;
        this.anoLancamento = anoLancamento;
        this.genero = genero;
        this.diretor = diretor;
    }

    // Getters
    public String getPosterUrl() { return posterUrl; }
    public String getSinopse() { return sinopse; }
    public int getAnoLancamento() { return anoLancamento; }
    public String getGenero() { return genero; }
    public String getDiretor() { return diretor; }
}