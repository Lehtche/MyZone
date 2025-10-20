package com.myzone.model;

public class Episodio extends Midia {
    private int temporada;
    private int episodio;
    private Serie serie;

    public Episodio(int id, String nome, Usuario cadastradoPor, int temporada, int episodio, Serie serie) {
        super(id, nome, cadastradoPor);
        this.temporada = temporada;
        this.episodio = episodio;
        this.serie = serie;
    }

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
        return super.toString() + " | Série: " + serie.getNome() + " | Temporada: " + temporada + " | Episódio: " + episodio;
    }
}
