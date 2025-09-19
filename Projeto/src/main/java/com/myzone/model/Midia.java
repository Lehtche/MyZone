// Classe genérica (superclasse)
package com.myzone.model;

public abstract class Midia {
    private int idMidia;
    private String titulo;
    private String capa;

    // Construtor
    public Midia(int idMidia, String titulo, String capa) {
        this.idMidia = idMidia;
        this.titulo = titulo;
        this.capa = capa;
    }

    // Getters e Setters
    public int getIdMidia() {
        return idMidia;
    }

    public void setIdMidia(int idMidia) {
        this.idMidia = idMidia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCapa() {
        return capa;
    }

    public void setCapa(String capa) {
        this.capa = capa;
    }

    // Método genérico que pode ser sobrescrito
    public abstract void exibirInfo();
}
