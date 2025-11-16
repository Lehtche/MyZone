package dev.JavaLovers.MyZone.dto;

public class LivroApiResponseDTO {
    private String nome; // <-- CAMPO ADICIONADO
    private String autor;
    private String genero;
    private String anoLancamento;
    private String posterUrl;
    private String sinopse;

    // Construtor atualizado (com 6 argumentos)
    public LivroApiResponseDTO(String nome, String autor, String genero, String anoLancamento, String posterUrl, String sinopse) {
        this.nome = nome; // <-- CAMPO ADICIONADO
        this.autor = autor;
        this.genero = genero;
        this.anoLancamento = anoLancamento;
        this.posterUrl = posterUrl;
        this.sinopse = sinopse;
    }

    // Getters
    public String getNome() { return nome; } // <-- GETTER ADICIONADO
    public String getAutor() { return autor; }
    public String getGenero() { return genero; }
    public String getAnoLancamento() { return anoLancamento; }
    public String getPosterUrl() { return posterUrl; }
    public String getSinopse() { return sinopse; }
}