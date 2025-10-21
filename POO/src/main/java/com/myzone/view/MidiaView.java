package com.myzone.view;

import java.util.List;
import java.util.Scanner; 
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
    private final UsuarioDAO usuarioDAO = new UsuarioDAO(); 
    
    private Usuario usuarioLogado; 

    public void menu(Usuario usuario) {
        this.usuarioLogado = usuario;
        
        if (this.usuarioLogado == null) {
            System.out.println("Erro: Ninguém está logado. Saindo do menu de mídias.");
            return;
        }

        int opcao;
        do {
            System.out.println("\n===== GERENCIAMENTO DE MÍDIAS (Logado como: " + usuarioLogado.getNome() + ") =====");
            System.out.println("1. Cadastrar nova mídia");
            System.out.println("2. Listar todas as mídias");
            System.out.println("3. Buscar mídia por ID");
            System.out.println("4. Atualizar mídia");
            System.out.println("5. Excluir mídia");
            System.out.println("0. Deslogar (Sair do menu)");
            System.out.print("Escolha: ");
            
            try {
                 opcao = sc.nextInt();
            } catch (Exception e) {
                 opcao = -1;
            }
            sc.nextLine(); 

            switch (opcao) {
                case 1 -> cadastrarMidia();
                case 2 -> listarMidias();
                case 3 -> buscarMidia();
                case 4 -> atualizarMidia();
                case 5 -> excluirMidia();
                case 0 -> System.out.println("Deslogando...");
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
        
        Usuario u = this.usuarioLogado;
        
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
            case 5 -> {
                System.out.print("Temporada: ");
                int temporada = sc.nextInt();
                System.out.print("Episódio: ");
                int episodio = sc.nextInt();
                System.out.print("ID da série (a qual este episódio pertence): ");
                int idSerie = sc.nextInt();
                sc.nextLine(); 

                Midia midiaAssociada = dao.buscarPorId(idSerie);

                if (midiaAssociada == null) {
                    System.out.println("ERRO: Nenhuma mídia encontrada com o ID " + idSerie + ". Cadastro de episódio cancelado.");
                    yield null; 
                    
                } else if (!(midiaAssociada instanceof Serie)) {
                    System.out.println("ERRO: A mídia " + idSerie + " é um " + midiaAssociada.getTipo() + ", não uma Série. Cadastro cancelado.");
                    yield null; 
                }

                Serie serie = (Serie) midiaAssociada;
                System.out.println("Associando episódio à série: " + serie.getNome());

                yield new Episodio(0, nome, u, temporada, episodio, serie);
            }
            default -> null;
        };

        if (midia != null) {
            dao.inserir(midia);
            System.out.println("Mídia cadastrada com sucesso!");
        } else {
            if (tipo != 5) {
                 System.out.println("Tipo inválido ou cadastro cancelado.");
            }
        }
    }

    private void listarMidias() {
        List<Midia> midias = dao.listarTodas(); 
        
        System.out.println("\nMídias cadastradas (no total):");
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

        if (m.getCadastradoPor().getId() != this.usuarioLogado.getId()) {
            System.out.println("ERRO: Você não tem permissão para editar esta mídia.");
            return;
        }

        System.out.println("\nEditando mídia: " + m.getNome());
        System.out.print("Novo nome (" + m.getNome() + "): ");
        String novoNome = sc.nextLine();
        if (!novoNome.trim().isEmpty()) {
            m.setNome(novoNome);
        }

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

            System.out.println("Série atual: " + e.getSerie().getNome() + " (ID: " + e.getSerie().getId() + ")");
            System.out.print("Digite o NOVO ID da série (ou deixe em branco para manter): ");
            String idSerieStr = sc.nextLine();

            if (!idSerieStr.trim().isEmpty()) {
                int idSerie = Integer.parseInt(idSerieStr);
                Midia midiaSerie = dao.buscarPorId(idSerie);

                if (midiaSerie != null && midiaSerie instanceof Serie) {
                    e.setSerie((Serie) midiaSerie); 
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

        Midia m = dao.buscarPorId(id);
        if (m == null) {
            System.out.println("Mídia não encontrada!");
            return;
        }
        if (m.getCadastradoPor().getId() != this.usuarioLogado.getId()) {
            System.out.println("ERRO: Você não tem permissão para excluir esta mídia.");
            return;
        }

        dao.excluir(id);
        System.out.println("Mídia excluída com sucesso!");
    }

    private void exibirDetalhesMidia(Midia m) {
        if (m == null) return;
        
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