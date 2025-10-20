// src/main/java/com/myzone/controller/ControleMidia.java
package com.myzone.controller;

import java.util.List;

import com.myzone.dao.MidiaDAO;
import com.myzone.model.Midia;
import com.myzone.model.Usuario;

public class ControleMidia {
    private MidiaDAO midiaDAO;

    public ControleMidia() {
        this.midiaDAO = new MidiaDAO();
    }

    public void cadastrarMidia(Midia midia, Usuario usuario) {
        if (midia == null || usuario == null) {
            System.out.println("⚠️ Dados inválidos para cadastro.");
            return;
        }

        midia.setCadastradoPor(usuario);
        midiaDAO.inserir(midia);
        System.out.println("✅ Mídia cadastrada com sucesso.");
    }

    public void listarMidias() {
        List<Midia> midias = midiaDAO.listarTodas();
        if (midias.isEmpty()) {
            System.out.println("⚠️ Nenhuma mídia cadastrada.");
            return;
        }
        midias.forEach(System.out::println);
    }

    public Midia buscarMidia(int id) {
        Midia midia = midiaDAO.buscarPorId(id);
        if (midia == null) {
            System.out.println("⚠️ Mídia não encontrada para o id " + id);
        }
        return midia;
    }

    public void editarMidia(Midia midia) {
        if (midia == null || midia.getId() <= 0) {
            System.out.println("⚠️ Dados inválidos para atualização.");
            return;
        }
        midiaDAO.atualizar(midia);
        System.out.println("✅ Mídia atualizada com sucesso.");
    }

    public void removerMidia(int id) {
        midiaDAO.excluir(id);
        System.out.println("🗑️ Mídia removida (se existia) com id " + id);
    }
}
