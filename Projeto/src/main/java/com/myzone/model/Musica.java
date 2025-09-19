package com.myzone.model;

public class Musica extends Midia {
    private String album;
    private String artista;

    public Musica(int idMidia, String titulo, String capa, String album, String artista) {
        super(idMidia, titulo, capa);
        this.album = album;
        this.artista = artista;
    }

    @Override
    public void exibirInfo() {
        System.out.println("Música: " + getTitulo() + " - Artista: " + artista);
    }
}