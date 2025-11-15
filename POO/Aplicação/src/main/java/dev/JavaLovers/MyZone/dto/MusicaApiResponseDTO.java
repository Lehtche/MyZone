package dev.JavaLovers.MyZone.dto;

public class MusicaApiResponseDTO {
    private String artista;
    private String album;
    private String posterUrl;
    private String dataEstreia;

    public MusicaApiResponseDTO(String artista, String album, String posterUrl, String dataEstreia) {
        this.artista = artista;
        this.album = album;
        this.posterUrl = posterUrl;
        this.dataEstreia = dataEstreia;
    }

    // Getters
    public String getArtista() { return artista; }
    public String getAlbum() { return album; }
    public String getPosterUrl() { return posterUrl; }
    public String getDataEstreia() { return dataEstreia; }
}