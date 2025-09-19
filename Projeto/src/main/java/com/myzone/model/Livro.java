package com.myzone.model;
// Subclasse que herda de Midia

public class Livro extends Midia {
    private String editora;
    private int numeroPaginas;

    public Livro(int idMidia, String titulo, String capa, String editora, int numeroPaginas) {
        super(idMidia, titulo, capa);
        this.editora = editora;
        this.numeroPaginas = numeroPaginas;
    }

    @Override
    public void exibirInfo() {
        System.out.println("Livro: " + getTitulo() + " - Editora: " + editora);
    }
}
