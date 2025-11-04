package dev.JavaLovers.MyZone.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "livro")
public class Livro extends Midia {
    private String autor;
    private String genero;

    public Livro() {};

    public Livro(String nome, Usuario cadastradoPor, String autor, String genero) {
        super(nome, cadastradoPor);
        this.autor = autor;
        this.genero = genero;
    }

    public String getAutor() { 
        return autor; 
    }
    public void setAutor(String autor) { 
        this.autor = autor; 
    }
    public String getGenero() { 
        return genero; 
    }
    public void setGenero(String genero) { 
        this.genero = genero; 
    }

    @Override
    public String getTipo() {
        return "LIVRO";
    }

    @Override
    public String toString() {
        return super.toString() + " | Autor: " + autor + " | Gênero: " + genero;
    }
}
