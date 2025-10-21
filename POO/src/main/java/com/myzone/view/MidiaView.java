package com.myzone.view;

import com.myzone.dao.MidiaDAO;
import com.myzone.model.*;

import java.util.List;
import java.util.Scanner;

public class MidiaView {
    private final Scanner sc = new Scanner(System.in);
    private final MidiaDAO dao = new MidiaDAO();

    public void menu() {
        int opcao;
        do {
            System.out.println("\n===== GERENCIAMENTO DE MÍDIAS =====");
            System.out.println("1. Cadastrar nova mídia");
            System.out.println("2. Listar todas as mídias");
            System.out.println("3. Buscar mídia por ID");
            System.out.println("4. Atualizar mídia");
            System.out.println("5. Excluir mídia");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> cadastrarMidia();
                case 2 -> listarMidias();
                case 3 -> buscarMidia();
                case 4 -> atualizarMidia();
                case 5 -> excluirMidia();
                case 0 -> System.out.println("Encerrando o programa...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void cadastrarMidia() {
        System.out.println("\nTipos disponíveis:");
        System.out.println("1. Filme");
        System.out.println("2. Livro");
        System.out.println("3. Música");
        System.out.println("4. Série");
        System.out.println("5. Episódio");
        System.out.print("Escolha o tipo: ");
        int tipo = sc.nextInt();
        sc.nextLine();

        System.out.print("Nome da mídia: ");
        String nome = sc.nextLine();
        System.out.print("ID do usuário que cadastrou: ");
        int idUsuario = sc.nextInt();
        sc.nextLine();

        Usuario u = new Usuario(idUsuario, "Usuário Simulado", "email@teste.com", "senha"); // Simulação

        Midia midia = switch (tipo) {
            case 1 -> {
                System.out.print("Diretor: ");
                String diretor = sc.nextLine();
                System.out.print("Duração (min): ");
                int duracao = sc.nextInt();
                yield new Filme(0, nome, u, diretor, duracao);
            }
            case 2 -> {
                System.out.print("Autor: ");
                String autor = sc.nextLine();
                System.out.print("Páginas: ");
                int paginas = sc.nextInt();
                yield new Livro(0, nome, u, autor, paginas);
            }
            case 3 -> {
                System.out.print("Artista: ");
                String artista = sc.nextLine();
                System.out.print("Duração (min): ");
                int duracao = sc.nextInt();
                yield new Musica(0, nome, u, artista, duracao);
            }
            case 4 -> {
                System.out.print("Nº de temporadas: ");
                int temporadas = sc.nextInt();
                yield new Serie(0, nome, u, temporadas);
            }
            case 5 -> {
                System.out.print("Temporada: ");
                int temporada = sc.nextInt();
                System.out.print("Episódio: ");
                int episodio = sc.nextInt();
                System.out.print("ID da série: ");
                int idSerie = sc.nextInt();
                Serie serie = new Serie(idSerie, "Série Referência", u, 0);
                yield new Episodio(0, nome, u, temporada, episodio, serie);
            }
            default -> null;
        };

        if (midia != null) {
            dao.inserir(midia);
            System.out.println("Mídia cadastrada com sucesso!");
        } else {
            System.out.println("Tipo inválido!");
        }
    }

    private void listarMidias() {
        List<Midia> midias = dao.listarTodas();
        System.out.println("\nMídias cadastradas:");
        if (midias.isEmpty()) {
            System.out.println("(Nenhuma mídia encontrada)");
        } else {
            for (Midia m : midias) {
                exibirDetalhesMidia(m);
            }
        }
    }

    private void buscarMidia() {
        System.out.print("\nDigite o ID da mídia: ");
        int id = sc.nextInt();
        Midia m = dao.buscarPorId(id);
        if (m != null) {
            System.out.println("\nMídia encontrada:");
            exibirDetalhesMidia(m);
        } else {
            System.out.println("Nenhuma mídia encontrada com esse ID!");
        }
    }

    private void atualizarMidia() {
        listarMidias();
        System.out.print("\nDigite o ID da mídia para atualizar: ");
        int id = sc.nextInt();
        sc.nextLine();

        Midia m = dao.buscarPorId(id);
        if (m == null) {
            System.out.println("Mídia não encontrada!");
            return;
        }

        System.out.print("Novo nome: ");
        String novoNome = sc.nextLine();
        m.setNome(novoNome);

        // Agora, pede os campos específicos baseado no TIPO da mídia
        
        if (m instanceof Filme f) {
            System.out.print("Novo Diretor: ");
            f.setDiretor(sc.nextLine()); // Atualiza o objeto 'f' (que é o mesmo que 'm')
            
            System.out.print("Nova Duração (min): ");
            f.setDuracao(sc.nextInt());
            sc.nextLine(); // Consumir newline
            
        } else if (m instanceof Livro l) {
            System.out.print("Novo Autor: ");
            l.setAutor(sc.nextLine());
            
            System.out.print("Novas Páginas: ");
            l.setPaginas(sc.nextInt());
            sc.nextLine();
            
        } else if (m instanceof Musica mu) {
            System.out.print("Novo Artista: ");
            mu.setArtista(sc.nextLine());
            
            System.out.print("Nova Duração (min): ");
            mu.setDuracao(sc.nextInt());
            sc.nextLine();
            
        } else if (m instanceof Serie s) {
            System.out.print("Nº de temporadas: ");
            s.setTemporadas(sc.nextInt());
            sc.nextLine();
            
        } else if (m instanceof Episodio e) {
            System.out.print("Nova Temporada: ");
            e.setTemporada(sc.nextInt());
            sc.nextLine();
            
            System.out.print("Novo Episódio: ");
            e.setEpisodio(sc.nextInt());
            sc.nextLine();
            
            System.out.print("Novo ID da série: ");
            int idSerie = sc.nextInt();
            sc.nextLine();
            
            // Atualiza a referência da série dentro do episódio
            // (O DAO só precisa do ID, então podemos só atualizar o ID do objeto)
            e.getSerie().setId(idSerie); 
        }

        dao.atualizar(m);
        System.out.println("Mídia atualizada com sucesso!");
    }

    private void excluirMidia() {
        listarMidias();
        System.out.print("\nDigite o ID da mídia para excluir: ");
        int id = sc.nextInt();
        dao.excluir(id);
        System.out.println("Mídia excluída com sucesso!");
    }

    /** 🔍 Exibe informações específicas dependendo do tipo da mídia */
    private void exibirDetalhesMidia(Midia m) {
        System.out.println("\nID: " + m.getId() +
                "\nTipo: " + m.getTipo() +
                "\nNome: " + m.getNome());

        if (m instanceof Filme f) {
            System.out.println("Diretor: " + f.getDiretor());
            System.out.println("Duração: " + f.getDuracao() + " min");
        } else if (m instanceof Livro l) {
            System.out.println("Autor: " + l.getAutor());
            System.out.println("Páginas: " + l.getPaginas());
        } else if (m instanceof Musica mu) {
            System.out.println("Artista: " + mu.getArtista());
            System.out.println("Duração: " + mu.getDuracao() + " min");
        } else if (m instanceof Serie s) {
            System.out.println("Temporadas: " + s.getTemporadas());
        } else if (m instanceof Episodio e) {
            System.out.println("Série: " + e.getSerie().getNome());
            System.out.println("Temporada: " + e.getTemporada());
            System.out.println("Episódio: " + e.getEpisodio());
        }
    }
}
