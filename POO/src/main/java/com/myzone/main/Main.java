package com.myzone.main;

import com.myzone.controller.ControleUsuario;
import com.myzone.model.Usuario;
import com.myzone.view.MidiaView;
import com.myzone.view.UsuarioView;

import java.util.Scanner;

/**
 * Ponto de entrada principal da aplicação MyZone.
 * Controla o fluxo de login/cadastro e o acesso ao menu de mídias.
 */
public class Main {

    public static void main(String[] args) {
        // --- Configuração Inicial ---
        Scanner sc = new Scanner(System.in);
        
        // 1. Inicializa o Controller e a View de Usuário
        ControleUsuario controleUsuario = new ControleUsuario();
        UsuarioView usuarioView = new UsuarioView(controleUsuario);
        
        // 2. Inicializa a View de Mídia (que tem seus próprios DAOs)
        MidiaView midiaView = new MidiaView();
        
        Usuario usuarioLogado = null;

        // --- Loop Principal do Programa ---
        while (true) {
            
            // Se NINGUÉM estiver logado, mostra o menu de Login/Cadastro
            if (usuarioLogado == null) {
                System.out.println("\n===== BEM-VINDO AO MYZONE =====");
                System.out.println("1. Fazer Login");
                System.out.println("2. Cadastrar Novo Usuário");
                System.out.println("0. Sair do Programa");
                System.out.print("Escolha: ");
                
                String opcao = sc.nextLine();

                switch (opcao) {
                    case "1":
                        // O método login() de UsuarioView pede email/senha e retorna o Usuário
                        usuarioLogado = usuarioView.login();
                        if (usuarioLogado != null) {
                            System.out.println("Login bem-sucedido! Bem-vindo, " + usuarioLogado.getNome());
                        } else {
                            System.out.println("Email ou senha inválidos. Tente novamente.");
                        }
                        break;
                    case "2":
                        // O método cadastrarUsuario() pede os dados e o controller os salva
                        usuarioView.cadastrarUsuario();
                        // Nota: O seu método cadastrarUsuario() retorna null,
                        // então pedimos para o usuário fazer login manualmente.
                        System.out.println("Usuário cadastrado com sucesso! Por favor, faça login.");
                        break;
                    case "0":
                        System.out.println("Encerrando o programa...");
                        sc.close();
                        return; // Encerra o método main e o programa
                    default:
                        System.out.println("Opção inválida.");
                        break;
                }
            } 
            // Se ALGUÉM estiver logado, mostra o menu de Mídias
            else {
                // Passa o usuário logado para o menu de mídias
                midiaView.menu(usuarioLogado);
                
                // Quando o usuário sai do menu(0) de mídias, ele é "deslogado"
                // e volta para o menu de Login/Cadastro.
                System.out.println("Você foi desconectado.");
                usuarioLogado = null; 
            }
        }
    }
}