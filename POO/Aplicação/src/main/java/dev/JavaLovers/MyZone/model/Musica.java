package dev.JavaLovers.MyZone.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "musica")
public class Musica extends Midia {
    private String artista;
    private String album;
    private String dataEstreia;

    public Musica() {};

    public Musica(String nome, Usuario cadastradoPor, String artista, String album, String dataEstreia) {
        super(nome, cadastradoPor);
        this.artista = artista;
        this.album = album;
        this.dataEstreia = dataEstreia;
    }

    public String getArtista() { 
        return artista; 
    }
    public void setArtista(String artista) { this.artista = artista; }
    
    public String getAlbum() { 
        return album; 
    }
    public void setAlbum(String album) { 
        this.album = album; 
    }
    public String getDataEstreia() { 
        return dataEstreia; 
    }
    public void setDataEstreia(String dataEstreia) { 
        this.dataEstreia = dataEstreia; 
    }

    @Override
    public String getTipo() {
        return "MUSICA";
    }

    @Override
    public String toString() {
        return super.toString() + " | Artista: " + artista + " | Album: " + album + " | Data de Estreia: " + dataEstreia;
    }
}
