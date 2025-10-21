package com.myzone.model;

public abstract class Midia {
    protected int id;
    protected String nome;
    protected Usuario cadastradoPor;

    public Midia(int id, String nome, Usuario cadastradoPor) {
        this.id = id;
        this.nome = nome;
        this.cadastradoPor = cadastradoPor;
    }

    public Midia() {}

    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }
    public String getNome() { 
        return nome; 
    }
    public void setNome(String nome) { 
        this.nome = nome; 
    }
    public Usuario getCadastradoPor() { 
        return cadastradoPor; 
    }
    public void setCadastradoPor(Usuario cadastradoPor) { 
        this.cadastradoPor = cadastradoPor; 
    }

    public abstract String getTipo();
}
