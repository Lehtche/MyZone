package dev.JavaLovers.MyZone.dto;

public class TmdbResponseDTO {
    private String posterUrl;
    private String sinopse;
    private int anoLancamento;
    private String genero; // <-- Campo para o gênero

    public TmdbResponseDTO(String posterUrl, String sinopse, int anoLancamento, String genero) {
        this.posterUrl = posterUrl;
        this.sinopse = sinopse;
        this.anoLancamento = anoLancamento;
        this.genero = genero;
    }

    // Getters
    public String getPosterUrl() { return posterUrl; }
    public String getSinopse() { return sinopse; }
    public int getAnoLancamento() { return anoLancamento; }
    public String getGenero() { return genero; }
}