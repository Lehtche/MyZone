package dev.JavaLovers.MyZone.dto;

public class MusicaApiResponseDTO {

    private String nome;
    private String artista;
    private String album;
    private String posterUrl;
    private String dataEstreia;
    private String sinopse;

    public MusicaApiResponseDTO(String nome, String artista, String album,
                                String posterUrl, String dataEstreia, String sinopse) {
        this.nome = nome;
        this.artista = artista;
        this.album = album;
        this.posterUrl = posterUrl;
        this.dataEstreia = dataEstreia;
        this.sinopse = sinopse;
    }

    // Getters
    public String getNome() { return nome; }
    public String getArtista() { return artista; }
    public String getAlbum() { return album; }
    public String getPosterUrl() { return posterUrl; }
    public String getDataEstreia() { return dataEstreia; }
    public String getSinopse() { return sinopse; }
}
