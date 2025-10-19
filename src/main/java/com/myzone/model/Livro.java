package com.myzone.model;

public class Livro extends Midia {
    private String autor;
    private int paginas;

    public Livro(int id, String nome, Usuario cadastradoPor, String autor, int paginas) {
        super(id, nome, cadastradoPor);
        this.autor = autor;
        this.paginas = paginas;
    }

    public String getAutor() { 
        return autor; 
    }
    public void setAutor(String autor) { 
        this.autor = autor; 
    }
    public int getPaginas() { 
        return paginas; 
    }
    public void setPaginas(int paginas) { 
        this.paginas = paginas; 
    }

    @Override
    public String getTipo() {
        return "LIVRO";
    }

    @Override
    public String toString() {
        return super.toString() + " | Autor: " + autor + " | Páginas: " + paginas;
    }
}
