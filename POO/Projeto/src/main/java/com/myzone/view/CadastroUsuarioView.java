package com.myzone.view;

import java.util.List;
import java.util.Scanner;

import com.myzone.controller.ControleUsuario;
import com.myzone.model.Usuario;

public class CadastroUsuarioView {
    public static void main(String[] args) {
        ControleUsuario controller = new ControleUsuario();
        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n===== MENU USUÁRIO =====");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("3 - Buscar por ID");
            System.out.println("4 - Atualizar");
            System.out.println("5 - Deletar");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    System.out.print("Senha: ");
                    String senha = sc.nextLine();
                    controller.criarUsuario(nome, email, senha);
                    break;

                case 2:
                    List<Usuario> lista = controller.listarUsuarios();
                    lista.forEach(u -> System.out.println(u.getId() + " - " + u.getNome() + " - " + u.getEmail()));
                    break;

                case 3:
                    System.out.print("ID: ");
                    int idBusca = sc.nextInt();
                    Usuario u = controller.buscarUsuario(idBusca);
                    if (u != null) {
                        System.out.println("Nome: " + u.getNome());
                        System.out.println("Email: " + u.getEmail());
                    } else {
                        System.out.println("Usuário não encontrado!");
                    }
                    break;

                case 4:
                    System.out.print("ID do usuário: ");
                    int idAtualiza = sc.nextInt(); sc.nextLine();
                    System.out.print("Novo nome: ");
                    String novoNome = sc.nextLine();
                    System.out.print("Novo email: ");
                    String novoEmail = sc.nextLine();
                    System.out.print("Nova senha: ");
                    String novaSenha = sc.nextLine();
                    controller.atualizarUsuario(idAtualiza, novoNome, novoEmail, novaSenha);
                    break;

                case 5:
                    System.out.print("ID do usuário: ");
                    int idDeleta = sc.nextInt();
                    controller.deletarUsuario(idDeleta);
                    break;

                case 0:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);

        sc.close();
    }
}
