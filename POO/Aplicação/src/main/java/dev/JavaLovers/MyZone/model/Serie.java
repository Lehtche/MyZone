package dev.JavaLovers.MyZone.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "serie")
public class Serie extends Midia {
    private int temporadas;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL)
    private List<Episodio> episodios;

    public Serie() {
        super();
    }

    public Serie(String nome, Usuario cadastradoPor, int temporadas) {
        super(nome, cadastradoPor);
        this.temporadas = temporadas;
    }

    // Getters e Setters 
    public int getTemporadas() { 
        return temporadas; 
    }
    public void setTemporadas(int temporadas) { 
        this.temporadas = temporadas; 
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }
    public void setEpisodios(List<Episodio> episodios) {
        this.episodios = episodios;
    }

    @Override
    public String getTipo() {
        return "SERIE";
    }

    @Override
    public String toString() {
        return super.toString() + " | Temporadas: " + temporadas;
    }
}