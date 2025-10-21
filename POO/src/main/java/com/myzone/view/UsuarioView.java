package com.myzone.view;

import java.util.Scanner;

import com.myzone.controller.ControleUsuario;
import com.myzone.model.Usuario;

public class UsuarioView {
    private ControleUsuario usuarioController;
    private Scanner sc;

    public UsuarioView(ControleUsuario usuarioController) {
        this.usuarioController = usuarioController;
        this.sc = new Scanner(System.in);
    }

    public Usuario cadastrarUsuario() {
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Senha: ");
        String senha = sc.nextLine();
        usuarioController.cadastrarUsuario(nome, email, senha);
        return null;
    }

    public Usuario login() {
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Senha: ");
        String senha = sc.nextLine();
        return usuarioController.login(email, senha);
    }
}
