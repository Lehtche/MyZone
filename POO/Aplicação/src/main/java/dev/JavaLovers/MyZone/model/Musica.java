package dev.JavaLovers.MyZone.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate; // Importar

@Entity
@Table(name = "musica")
public class Musica extends Midia {
    private String artista;
    private String album;
    private LocalDate dataEstreia; // <-- MUDADO PARA LOCALDATE

    public Musica() {
        super();
    };

    // Getters e Setters
    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }
    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }
    public LocalDate getDataEstreia() { return dataEstreia; }
    public void setDataEstreia(LocalDate dataEstreia) { this.dataEstreia = dataEstreia; }

    @Override
    public String getTipo() { return "MUSICA"; }
}