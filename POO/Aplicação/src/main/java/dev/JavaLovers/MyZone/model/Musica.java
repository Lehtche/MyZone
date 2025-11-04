package dev.JavaLovers.MyZone.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "musica")
public class Musica extends Midia {
    private String artista;
    private String genero;

    public Musica() {};

    public Musica(String nome, Usuario cadastradoPor, String artista, String genero) {
        super(nome, cadastradoPor);
        this.artista = artista;
        this.genero = genero;
    }

    public String getArtista() { 
        return artista; 
    }
    public void setArtista(String artista) { this.artista = artista; }
    public String getGenero() { 
        return genero; 
    }
    public void setGenero(String genero) { 
        this.genero = genero; 
    }

    @Override
    public String getTipo() {
        return "MUSICA";
    }

    @Override
    public String toString() {
        return super.toString() + " | Artista: " + artista + " | Gênero: " + genero;
    }
}
