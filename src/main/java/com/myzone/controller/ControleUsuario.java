package com.myzone.controller;

import com.myzone.dao.UsuarioDAO;
import com.myzone.model.Usuario;

public class ControleUsuario {
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioLogado;

    public ControleUsuario() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Usuario login(String email, String senha) {
        Usuario usuario = usuarioDAO.autenticar(email, senha);
        if (usuario != null) {
            usuarioLogado = usuario;
            System.out.println("✅ Login realizado com sucesso! Bem-vindo, " + usuario.getNome());
        } else {
            System.out.println("❌ Email ou senha incorretos.");
        }
        return usuario;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void cadastrarUsuario(String nome, String email, String senha) {
        usuarioDAO.inserir(new Usuario(0, nome, email, senha));
        System.out.println("✅ Usuário cadastrado com sucesso!");
    }
}
