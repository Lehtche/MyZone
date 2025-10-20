package com.myzone.view;

import java.util.Scanner;

import com.myzone.controller.ControleMidia;
import com.myzone.model.Episodio;
import com.myzone.model.Filme;
import com.myzone.model.Livro;
import com.myzone.model.Musica;
import com.myzone.model.Serie;
import com.myzone.model.Usuario;

public class MidiaView {
    private ControleMidia midiaController;
    private Scanner sc;

    public MidiaView(ControleMidia midiaController) {
        this.midiaController = midiaController;
        this.sc = new Scanner(System.in);
    }

    public void cadastrarMidia(Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            System.out.println("❌ Você precisa estar logado para cadastrar mídias!");
            return;
        }

        System.out.println("Escolha o tipo de mídia: 1-Filme 2-Livro 3-Música 4-Série 5-Episódio");
        int tipo = sc.nextInt();
        sc.nextLine();
        System.out.print("Nome da mídia: ");
        String nome = sc.nextLine();

        switch (tipo) {
            case 1 -> {
                System.out.print("Diretor: ");
                String diretor = sc.nextLine();
                System.out.print("Duração (min): ");
                int duracao = sc.nextInt();
                sc.nextLine();
                Filme filme = new Filme(0, nome, usuarioLogado, diretor, duracao);
                midiaController.cadastrarMidia(filme, usuarioLogado);
            }
            case 2 -> {
                System.out.print("Autor: ");
                String autor = sc.nextLine();
                System.out.print("Número de páginas: ");
                int paginas = sc.nextInt();
                sc.nextLine();
                Livro livro = new Livro(0, nome, usuarioLogado, autor, paginas);
                midiaController.cadastrarMidia(livro, usuarioLogado);
            }
            case 3 -> {
                System.out.print("Artista: ");
                String artista = sc.nextLine();
                System.out.print("Duração (min): ");
                int duracao = sc.nextInt();
                sc.nextLine();
                Musica musica = new Musica(0, nome, usuarioLogado, artista, duracao);
                midiaController.cadastrarMidia(musica, usuarioLogado);
            }
            case 4 -> {
                System.out.print("Número de temporadas: ");
                int temp = sc.nextInt();
                sc.nextLine();
                Serie serie = new Serie(0, nome, usuarioLogado, temp);
                midiaController.cadastrarMidia(serie, usuarioLogado);
            }
            case 5 -> {
                System.out.print("ID da Série: ");
                int idSerie = sc.nextInt();
                sc.nextLine();
                System.out.print("Temporada: ");
                int temporada = sc.nextInt();
                sc.nextLine();
                System.out.print("Número do episódio: ");
                int epNum = sc.nextInt();
                sc.nextLine();
                Serie serieRef = new Serie(idSerie, "Série Referência", usuarioLogado, 0);
                Episodio episodio = new Episodio(0, nome, usuarioLogado, temporada, epNum, serieRef);
                midiaController.cadastrarMidia(episodio, usuarioLogado);
            }
            default -> System.out.println("❌ Tipo inválido.");
        }
    }

    public void listarMidias() {
        midiaController.listarMidias();
    }
}
