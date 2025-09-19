package com.myzone.model;

public class Filme extends Midia {
    private String diretor;
    private String elenco;

    public Filme(int idMidia, String titulo, String capa, String diretor, String elenco) {
        super(idMidia, titulo, capa);
        this.diretor = diretor;
        this.elenco = elenco;
    }

    @Override
    public void exibirInfo() {
        System.out.println("Filme: " + getTitulo() + " - Diretor: " + diretor);
    }
}
