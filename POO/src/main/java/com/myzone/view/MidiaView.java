package com.myzone.view;

import java.util.List;
import java.util.Scanner; // Importar o DAO do Usuário

import com.myzone.dao.MidiaDAO;
import com.myzone.dao.UsuarioDAO;
import com.myzone.model.Episodio;
import com.myzone.model.Filme;
import com.myzone.model.Livro;
import com.myzone.model.Midia;
import com.myzone.model.Musica;
import com.myzone.model.Serie;
import com.myzone.model.Usuario;

public class MidiaView {
    private final Scanner sc = new Scanner(System.in);
    private final MidiaDAO dao = new MidiaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO(); // Instanciar o DAO do Usuário

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

        // Buscar o usuário real
        Usuario u = usuarioDAO.buscarPorId(idUsuario);
        if (u == null) {
            System.out.println("ERRO: Usuário com ID " + idUsuario + " não encontrado. Cadastro cancelado.");
            return; // Aborta o cadastro
        }
        System.out.println("Cadastrando em nome do usuário: " + u.getNome());

        Midia midia = switch (tipo) {
            case 1 -> {
                System.out.print("Diretor: ");
                String diretor = sc.nextLine();
                System.out.print("Duração (min): ");
                int duracao = sc.nextInt();
                sc.nextLine();
                yield new Filme(0, nome, u, diretor, duracao);
            }
            case 2 -> {
                System.out.print("Autor: ");
                String autor = sc.nextLine();
                System.out.print("Páginas: ");
                int paginas = sc.nextInt();
                sc.nextLine();
                yield new Livro(0, nome, u, autor, paginas);
            }
            case 3 -> {
                System.out.print("Artista: ");
                String artista = sc.nextLine();
                System.out.print("Duração (min): ");
                int duracao = sc.nextInt();
                sc.nextLine();
                yield new Musica(0, nome, u, artista, duracao);
            }
            case 4 -> {
                System.out.print("Nº de temporadas: ");
                int temporadas = sc.nextInt();
                sc.nextLine();
                yield new Serie(0, nome, u, temporadas);
            }
            //Lógica para buscar a série real
            case 5 -> {
                System.out.print("Temporada: ");
                int temporada = sc.nextInt();
                System.out.print("Episódio: ");
                int episodio = sc.nextInt();
                System.out.print("ID da série (a qual este episódio pertence): ");
                int idSerie = sc.nextInt();
                sc.nextLine(); // <-- Consome o "Enter"

                // Busca a mídia com o ID informado
                Midia midiaAssociada = dao.buscarPorId(idSerie);

                // Validação: Verifica se a mídia existe E se é do tipo 'Serie'
                if (midiaAssociada == null) {
                    System.out.println("ERRO: Nenhuma mídia encontrada com o ID " + idSerie + ". Cadastro de episódio cancelado.");
                    yield null; // Aborta a criação
                    
                } else if (!(midiaAssociada instanceof Serie)) {
                    // Se encontrou, mas não é uma Série
                    System.out.println("ERRO: A mídia " + idSerie + " é um " + midiaAssociada.getTipo() + ", não uma Série. Cadastro cancelado.");
                    yield null; // Aborta a criação
                }

                // Se passou nas validações, faz o "cast"
                Serie serie = (Serie) midiaAssociada;
                System.out.println("Associando episódio à série: " + serie.getNome());

                // Cria o episódio usando o objeto 'serie' real
                yield new Episodio(0, nome, u, temporada, episodio, serie);
            }
            default -> null;
        };

        if (midia != null) {
            dao.inserir(midia);
            System.out.println("Mídia cadastrada com sucesso!");
        } else {
            // A mensagem de erro específica já foi dada no 'case 5'
            if (tipo != 5) {
                 System.out.println("Tipo inválido!");
            }
        }
    }

    private void listarMidias() {
        List<Midia> midias = dao.listarTodas();
        System.out.println("\n📋 Mídias cadastradas:");
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
        sc.nextLine();
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

        System.out.println("\nEditando mídia: " + m.getNome());
        System.out.print("Novo nome (" + m.getNome() + "): ");
        String novoNome = sc.nextLine();
        if (!novoNome.trim().isEmpty()) {
            m.setNome(novoNome); // Só atualiza se não for vazio
        }

        // Pede os campos específicos baseado no TIPO da mídia
        
        if (m instanceof Filme f) {
            System.out.print("Novo Diretor (" + f.getDiretor() + "): ");
            String novoDiretor = sc.nextLine();
            if (!novoDiretor.trim().isEmpty()) {
                 f.setDiretor(novoDiretor);
            }
            
            System.out.print("Nova Duração (min) (" + f.getDuracao() + "): ");
            String duracaoStr = sc.nextLine();
            if (!duracaoStr.trim().isEmpty()) {
                 f.setDuracao(Integer.parseInt(duracaoStr));
            }
            
        } else if (m instanceof Livro l) {
            System.out.print("Novo Autor (" + l.getAutor() + "): ");
            String novoAutor = sc.nextLine();
             if (!novoAutor.trim().isEmpty()) {
                l.setAutor(novoAutor);
             }
            
            System.out.print("Novas Páginas (" + l.getPaginas() + "): ");
            String paginasStr = sc.nextLine();
             if (!paginasStr.trim().isEmpty()) {
                l.setPaginas(Integer.parseInt(paginasStr));
             }
            
        } else if (m instanceof Musica mu) {
            System.out.print("Novo Artista (" + mu.getArtista() + "): ");
            String novoArtista = sc.nextLine();
            if (!novoArtista.trim().isEmpty()) {
                mu.setArtista(novoArtista);
            }
            
            System.out.print("Nova Duração (min) (" + mu.getDuracao() + "): ");
            String duracaoStr = sc.nextLine();
            if (!duracaoStr.trim().isEmpty()) {
                mu.setDuracao(Integer.parseInt(duracaoStr));
            }
            
        } else if (m instanceof Serie s) {
            System.out.print("Nº de temporadas (" + s.getTemporadas() + "): ");
            String temporadasStr = sc.nextLine();
             if (!temporadasStr.trim().isEmpty()) {
                s.setTemporadas(Integer.parseInt(temporadasStr));
             }
            
        } else if (m instanceof Episodio e) {
            System.out.print("Nova Temporada (" + e.getTemporada() + "): ");
            String temporadaStr = sc.nextLine();
            if (!temporadaStr.trim().isEmpty()) {
                 e.setTemporada(Integer.parseInt(temporadaStr));
            }
            
            System.out.print("Novo Episódio (" + e.getEpisodio() + "): ");
            String episodioStr = sc.nextLine();
            if (!episodioStr.trim().isEmpty()) {
                e.setEpisodio(Integer.parseInt(episodioStr));
            }

            // ATUALIZAR a série associada
            System.out.println("Série atual: " + e.getSerie().getNome() + " (ID: " + e.getSerie().getId() + ")");
            System.out.print("Digite o NOVO ID da série (ou deixe em branco para manter): ");
            String idSerieStr = sc.nextLine();

            if (!idSerieStr.trim().isEmpty()) {
                int idSerie = Integer.parseInt(idSerieStr);
                Midia midiaSerie = dao.buscarPorId(idSerie);

                if (midiaSerie != null && midiaSerie instanceof Serie) {
                    e.setSerie((Serie) midiaSerie); // Atualiza o objeto Série inteiro
                    System.out.println("Série associada atualizada para: " + midiaSerie.getNome());
                } else {
                    System.out.println("ID da série não encontrado ou inválido. A série NÃO foi alterada.");
                }
            }
        }

        dao.atualizar(m);
        System.out.println("Mídia atualizada com sucesso!");
    }

    private void excluirMidia() {
        listarMidias();
        System.out.print("\nDigite o ID da mídia para excluir: ");
        int id = sc.nextInt();
        sc.nextLine();
        dao.excluir(id);
        System.out.println("Mídia excluída com sucesso!");
    }

    // Exibe informações específicas dependendo do tipo da mídia
    private void exibirDetalhesMidia(Midia m) {
        if (m == null) return; // Segurança
        
        System.out.println("\n---------------------------------");
        System.out.println("ID: " + m.getId() + " (" + m.getTipo() + ")");
        System.out.println("Nome: " + m.getNome());

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
            System.out.println("Série: " + e.getSerie().getNome() + " (ID: " + e.getSerie().getId() + ")");
            System.out.println("Episódio: S" + e.getTemporada() + "E" + e.getEpisodio());
        }
        System.out.println("Cadastrado por: " + m.getCadastradoPor().getNome());
        System.out.println("---------------------------------");
    }
}