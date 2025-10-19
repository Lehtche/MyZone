package com.myzone.main;

import java.util.Scanner;

import com.myzone.controller.ControleMidia;
import com.myzone.controller.ControleUsuario;
import com.myzone.model.Usuario;
import com.myzone.view.MidiaView;
import com.myzone.view.UsuarioView;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ControleUsuario usuarioController = new ControleUsuario();
        ControleMidia midiaController = new ControleMidia();
        UsuarioView usuarioView = new UsuarioView(usuarioController);
        MidiaView midiaView = new MidiaView(midiaController);

        Usuario usuarioLogado = null;
        int opcao;

        do {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1 - Cadastrar Usuário");
            System.out.println("2 - Login");
            System.out.println("3 - Cadastrar Mídia");
            System.out.println("4 - Listar Mídias");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt();
            sc.nextLine(); // consumir \n

            switch (opcao) {
                case 1 -> usuarioView.cadastrarUsuario();
                case 2 -> usuarioLogado = usuarioView.login();
                case 3 -> midiaView.cadastrarMidia(usuarioLogado);
                case 4 -> midiaView.listarMidias();
                case 0 -> System.out.println("Saindo do sistema...");
                default -> System.out.println("❌ Opção inválida!");
            }

        } while (opcao != 0);

        sc.close();
    }
}
