package dev.JavaLovers.MyZone.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "serie")
public class Serie extends Midia {
    
    // --- CAMPO ATUALIZADO ---
    private String genero; // Trocado de 'int temporadas'

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL)
    private List<Episodio> episodios;

    public Serie() {
        super();
    }
    
    // Construtor atualizado (opcional, mas boa prática)
    public Serie(String nome, Usuario cadastradoPor, String genero) {
        super(nome, cadastradoPor);
        this.genero = genero;
    }

    // --- GETTERS/SETTERS ATUALIZADOS ---
    public String getGenero() {
        return genero;
    }
    public void setGenero(String genero) {
        this.genero = genero;
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
        return super.toString() + " | Gênero: " + genero;
    }
}