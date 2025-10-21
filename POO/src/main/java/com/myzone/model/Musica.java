package com.myzone.model;

public class Musica extends Midia {
    private String artista;
    private int duracao;

    public Musica(int id, String nome, Usuario cadastradoPor, String artista, int duracao) {
        super(id, nome, cadastradoPor);
        this.artista = artista;
        this.duracao = duracao;
    }

    public String getArtista() { 
        return artista; 
    }
    public void setArtista(String artista) { this.artista = artista; }
    public int getDuracao() { 
        return duracao; 
    }
    public void setDuracao(int duracao) { 
        this.duracao = duracao; 
    }

    @Override
    public String getTipo() {
        return "MUSICA";
    }

    @Override
    public String toString() {
        return super.toString() + " | Artista: " + artista + " | Duração: " + duracao + "min";
    }
}
