package dev.JavaLovers.MyZone.dto;

public class MusicaDTO {

    private String nome;
    private String artista;
    private String album;
    private int anoEstreia; 
    private int nota;
    private String comentario;
    private String posterUrl;
    private String sinopse;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public int getAnoEstreia() { return anoEstreia; } 
    public void setAnoEstreia(int anoEstreia) { this.anoEstreia = anoEstreia; } 

    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }
}