package com.myzone.model;

import java.util.List;

public class Serie extends Midia {
    private String status;
    private String diretor;
    private List<Episodio> episodios; // associação com Episodio

    public Serie(int idMidia, String titulo, String capa, String status, String diretor) {
        super(idMidia, titulo, capa);
        this.status = status;
        this.diretor = diretor;
    }

    public void adicionarEpisodio(Episodio episodio) {
        episodios.add(episodio);
    }

    @Override
    public void exibirInfo() {
        System.out.println("Série: " + getTitulo() + " - Diretor: " + diretor);
    }
}
