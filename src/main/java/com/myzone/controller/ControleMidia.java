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
        if (usuario == null) {
            System.out.println("❌ Você precisa estar logado para cadastrar mídias!");
            return;
        }
        midia.setCadastradoPor(usuario);
        midiaDAO.inserir(midia);
    }

    public void listarMidias() {
        List<Midia> midias = midiaDAO.listarTodas();
        if (midias.isEmpty()) {
            System.out.println("⚠️ Nenhuma mídia cadastrada.");
            return;
        }
        midias.forEach(System.out::println);
    }
}
