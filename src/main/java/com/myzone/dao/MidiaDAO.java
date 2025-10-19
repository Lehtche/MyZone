package com.myzone.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.myzone.model.Episodio;
import com.myzone.model.Filme;
import com.myzone.model.Livro;
import com.myzone.model.Midia;
import com.myzone.model.Musica;
import com.myzone.model.Serie;
import com.myzone.model.Usuario;
import com.myzone.util.Conexao;

public class MidiaDAO {

    public void inserir(Midia midia) {
        try (Connection conn = Conexao.getConexao()) {
            // 1️⃣ Inserir na tabela base
            String sqlBase = "INSERT INTO midia (nome, idUsuario) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, midia.getNome());
            stmt.setInt(2, midia.getCadastradoPor().getId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            int idGerado = rs.getInt(1);
            midia.setId(idGerado);

            // 2️⃣ Inserir na tabela específica
            if (midia instanceof Filme f) {
                String sql = "INSERT INTO filme (id, diretor, duracao) VALUES (?, ?, ?)";
                PreparedStatement s = conn.prepareStatement(sql);
                s.setInt(1, idGerado);
                s.setString(2, f.getDiretor());
                s.setInt(3, f.getDuracao());
                s.executeUpdate();
            } else if (midia instanceof Livro l) {
                String sql = "INSERT INTO livro (id, autor, paginas) VALUES (?, ?, ?)";
                PreparedStatement s = conn.prepareStatement(sql);
                s.setInt(1, idGerado);
                s.setString(2, l.getAutor());
                s.setInt(3, l.getPaginas());
                s.executeUpdate();
            } else if (midia instanceof Musica m) {
                String sql = "INSERT INTO musica (id, artista, duracao) VALUES (?, ?, ?)";
                PreparedStatement s = conn.prepareStatement(sql);
                s.setInt(1, idGerado);
                s.setString(2, m.getArtista());
                s.setInt(3, m.getDuracao());
                s.executeUpdate();
            } else if (midia instanceof Serie s) {
                String sql = "INSERT INTO serie (id, temporadas) VALUES (?, ?)";
                PreparedStatement st = conn.prepareStatement(sql);
                st.setInt(1, idGerado);
                st.setInt(2, s.getTemporadas());
                st.executeUpdate();
            } else if (midia instanceof Episodio e) {
                String sql = "INSERT INTO episodio (id, temporada, episodio, idSerie) VALUES (?, ?, ?, ?)";
                PreparedStatement st = conn.prepareStatement(sql);
                st.setInt(1, idGerado);
                st.setInt(2, e.getTemporada());
                st.setInt(3, e.getEpisodio());
                st.setInt(4, e.getSerie().getId());
                st.executeUpdate();
            }

            System.out.println("✅ Mídia cadastrada com sucesso!");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Listar todas as mídias
    public List<Midia> listarTodas() {
        List<Midia> midias = new ArrayList<>();
        String sqlBase = "SELECT * FROM midia m " +
                         "LEFT JOIN filme f ON m.id=f.id " +
                         "LEFT JOIN livro l ON m.id=l.id " +
                         "LEFT JOIN musica mu ON m.id=mu.id " +
                         "LEFT JOIN serie s ON m.id=s.id " +
                         "LEFT JOIN episodio e ON m.id=e.id " +
                         "JOIN usuario u ON m.idUsuario = u.id";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sqlBase);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha")
                );

                Midia midia;
                if (rs.getString("diretor") != null) {
                    midia = new Filme(rs.getInt("id"), rs.getString("nome"), usuario, rs.getString("diretor"), rs.getInt("duracao"));
                } else if (rs.getString("autor") != null) {
                    midia = new Livro(rs.getInt("id"), rs.getString("nome"), usuario, rs.getString("autor"), rs.getInt("paginas"));
                } else if (rs.getString("artista") != null) {
                    midia = new Musica(rs.getInt("id"), rs.getString("nome"), usuario, rs.getString("artista"), rs.getInt("duracao"));
                } else if (rs.getInt("temporadas") != 0) {
                    midia = new Serie(rs.getInt("id"), rs.getString("nome"), usuario, rs.getInt("temporadas"));
                } else {
                    Serie serie = new Serie(rs.getInt("idSerie"), "Série Referência", usuario, 0); // apenas referência
                    midia = new Episodio(rs.getInt("id"), rs.getString("nome"), usuario, rs.getInt("temporada"), rs.getInt("episodio"), serie);
                }

                midias.add(midia);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return midias;
    }
}
