package com.myzone.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.myzone.dao.UsuarioDAO;
import com.myzone.model.Usuario;
import com.myzone.util.Conexao;

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
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário encontrado no banco de dados.");
        }
        return usuarios;
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
    
    public void limparTabela() {
    String sql = "TRUNCATE TABLE usuario";

    try (Connection conn = Conexao.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.executeUpdate();
        System.out.println("Tabela 'usuario' limpa e AUTO_INCREMENT resetado!");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}
