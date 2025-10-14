package com.myzone.controller;

import java.util.List;

import com.myzone.dao.UsuarioDAO;
import com.myzone.model.Usuario;

public class ControleUsuario {
    private UsuarioDAO usuarioDAO;

    public ControleUsuario() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public void criarUsuario(String nome, String email, String senha) {
        Usuario usuario = new Usuario(0, nome, email, senha);
        usuarioDAO.inserir(usuario);
        System.out.println("Usuário criado com sucesso!");
    }

    public Usuario buscarUsuario(int id) {
        return usuarioDAO.buscarPorId(id);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listarTodos();
    }

    public void atualizarUsuario(int id, String nome, String email, String senha) {
        Usuario usuario = new Usuario(id, nome, email, senha);
        usuarioDAO.atualizar(usuario);
        System.out.println("Usuário atualizado com sucesso!");
    }

    public void deletarUsuario(int id) {
        usuarioDAO.deletar(id);
        System.out.println("Usuário removido com sucesso!");
    }
}
