package dev.JavaLovers.MyZone.dto;

public class MusicaApiResponseDTO {

    private String nome;
    private String artista;
    private String album;
    private String posterUrl;
    private String anoEstreia; 
    private String sinopse;

    public MusicaApiResponseDTO(String nome, String artista, String album,
                                String posterUrl, String anoEstreia, String sinopse) {
        this.nome = nome;
        this.artista = artista;
        this.album = album;
        this.posterUrl = posterUrl;
        this.anoEstreia = anoEstreia;
        this.sinopse = sinopse;
    }

    // Getters
    public String getNome() { return nome; }
    public String getArtista() { return artista; }
    public String getAlbum() { return album; }
    public String getPosterUrl() { return posterUrl; }
    public String getAnoEstreia() { return anoEstreia; }
    public String getSinopse() { return sinopse; }
}