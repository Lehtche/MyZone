package com.myzone.model;

public class Avaliacao {
    private int id;
    private Usuario Usuario;
    private Midia midia;
    private int nota;
    private String comentario;
    private String dataAvaliacao;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Usuario getUsuario() {
        return Usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.Usuario = usuario;
    }
    public Midia getMidia() {
        return midia;
    }
    public void setMidia(Midia midia) {
        this.midia = midia;
    }
    public int getNota() {
        return nota;
    }
    public void setNota(int nota) {
        this.nota = nota;
    }
    public String getComentario() {
        return comentario;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    public String getDataAvaliacao() {
        return dataAvaliacao;
    }
    public void setDataAvaliacao(String dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }
}

