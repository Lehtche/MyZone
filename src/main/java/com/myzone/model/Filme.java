package com.myzone.model;

public class Filme extends Midia {
    private String diretor;
    private int duracao;

    public Filme(int id, String nome, Usuario cadastradoPor, String diretor, int duracao) {
        super(id, nome, cadastradoPor);
        this.diretor = diretor;
        this.duracao = duracao;
    }

    public String getDiretor() { 
        return diretor; 
    }
    public void setDiretor(String diretor) { 
        this.diretor = diretor; 
    }
    public int getDuracao() { 
        return duracao; 
    }
    public void setDuracao(int duracao) { 
        this.duracao = duracao; 
    }

    @Override
    public String getTipo() {
        return "FILME";
    }

    @Override
    public String toString() {
        return super.toString() + " | Diretor: " + diretor + " | Duração: " + duracao + "min";
    }
}
