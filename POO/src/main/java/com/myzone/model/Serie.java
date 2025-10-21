package com.myzone.model;

public class Serie extends Midia {
    private int temporadas;

    public Serie(int id, String nome, Usuario cadastradoPor, int temporadas) {
        super(id, nome, cadastradoPor);
        this.temporadas = temporadas;
    }

    public int getTemporadas() { 
        return temporadas; 
    }
    public void setTemporadas(int temporadas) { 
        this.temporadas = temporadas; 
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
