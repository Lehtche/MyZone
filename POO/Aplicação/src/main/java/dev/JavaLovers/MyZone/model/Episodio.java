package dev.JavaLovers.MyZone.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "episodio")
public class Episodio extends Midia {
    private int temporada;
    private int episodio;

    // --- CORREÇÃO 1: Definindo o relacionamento ---
    @ManyToOne // Muitos Episódios para Uma Série
    @JoinColumn(name = "serie_id") // Nome da coluna da chave estrangeira no banco
    private Serie serie;

    // --- CORREÇÃO 2: Construtor vazio (obrigatório pelo JPA) ---
    public Episodio() {
        super();
    }

    // Seu construtor está perfeito
    public Episodio(String nome, Usuario cadastradoPor, int temporada, int episodio, Serie serie) {
        super(nome, cadastradoPor);
        this.temporada = temporada;
        this.episodio = episodio;
        this.serie = serie;
    }

    // Getters e Setters (seu código aqui estava correto)
    public int getTemporada() { 
        return temporada; 
    }
    public void setTemporada(int temporada) { 
        this.temporada = temporada; 
    }
    public int getEpisodio() { 
        return episodio; 
    }
    public void setEpisodio(int episodio) { 
        this.episodio = episodio; 
    }
    public Serie getSerie() { 
        return serie; 
    }
    public void setSerie(Serie serie) { 
        this.serie = serie; 
    }

    @Override
    public String getTipo() {
        return "EPISODIO";
    }

    @Override
    public String toString() {
        // Pequena proteção contra NullPointerException se a série for nula
        String nomeSerie = (serie != null) ? serie.getNome() : "N/A";
        return super.toString() + " | Série: " + nomeSerie + " | Temporada: " + temporada + " | Episódio: " + episodio;
    }
}