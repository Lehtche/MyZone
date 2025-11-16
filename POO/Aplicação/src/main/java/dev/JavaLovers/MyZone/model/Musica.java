package dev.JavaLovers.MyZone.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "musica")
public class Musica extends Midia {

    private String artista;
    private String album;
    private int anoEstreia; 

    public Musica() {
        super();
    }

    // Getters e Setters
    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public int getAnoEstreia() { return anoEstreia; } 
    public void setAnoEstreia(int anoEstreia) { this.anoEstreia = anoEstreia; } 

    @Override
    public String getTipo() {
        return "MUSICA";
    }
}