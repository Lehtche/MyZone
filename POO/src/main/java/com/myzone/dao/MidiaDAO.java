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
        String sql = "INSERT INTO midia (nome, idUsuario) VALUES (?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, midia.getNome());
            stmt.setInt(2, midia.getCadastradoPor().getId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            int idGerado = -1;
            if (rs.next()) {
                idGerado = rs.getInt(1);
            }

            // Insere na tabela específica
            if (midia instanceof Filme f) {
                String sqlF = "INSERT INTO filme (id, diretor, duracao) VALUES (?, ?, ?)";
                try (PreparedStatement s = conn.prepareStatement(sqlF)) {
                    s.setInt(1, idGerado);
                    s.setString(2, f.getDiretor());
                    s.setInt(3, f.getDuracao());
                    s.executeUpdate();
                }
            } else if (midia instanceof Livro l) {
                String sqlL = "INSERT INTO livro (id, autor, paginas) VALUES (?, ?, ?)";
                try (PreparedStatement s = conn.prepareStatement(sqlL)) {
                    s.setInt(1, idGerado);
                    s.setString(2, l.getAutor());
                    s.setInt(3, l.getPaginas());
                    s.executeUpdate();
                }
            } else if (midia instanceof Musica m) {
                String sqlM = "INSERT INTO musica (id, artista, duracao) VALUES (?, ?, ?)";
                try (PreparedStatement s = conn.prepareStatement(sqlM)) {
                    s.setInt(1, idGerado);
                    s.setString(2, m.getArtista());
                    s.setInt(3, m.getDuracao());
                    s.executeUpdate();
                }
            } else if (midia instanceof Serie sObj) {
                String sqlS = "INSERT INTO serie (id, temporadas) VALUES (?, ?)";
                try (PreparedStatement st = conn.prepareStatement(sqlS)) {
                    st.setInt(1, idGerado);
                    st.setInt(2, sObj.getTemporadas());
                    st.executeUpdate();
                }
            } else if (midia instanceof Episodio e) {
                String sqlE = "INSERT INTO episodio (id, temporada, episodio, idSerie) VALUES (?, ?, ?, ?)";
                try (PreparedStatement st = conn.prepareStatement(sqlE)) {
                    st.setInt(1, idGerado);
                    st.setInt(2, e.getTemporada());
                    st.setInt(3, e.getEpisodio());
                    st.setInt(4, e.getSerie().getId());
                    st.executeUpdate();
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

// atualiza Tabela midia e tabela específica
    public void atualizar(Midia midia) {
        String sql = "UPDATE midia SET nome = ?, idUsuario = ? WHERE id = ?";

        try (Connection conn = Conexao.getConexao()) {
            
            // 1. Atualiza a tabela base 'midia'
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, midia.getNome());
                stmt.setInt(2, midia.getCadastradoPor().getId());
                stmt.setInt(3, midia.getId());
                stmt.executeUpdate();
            }

            // 2. Atualiza a tabela específica correspondente
            
            if (midia instanceof Filme f) {
                String sqlF = "UPDATE filme SET diretor = ?, duracao = ? WHERE id = ?";
                try (PreparedStatement s = conn.prepareStatement(sqlF)) {
                    s.setString(1, f.getDiretor());
                    s.setInt(2, f.getDuracao());
                    s.setInt(3, midia.getId()); // Condição WHERE
                    s.executeUpdate();
                }
            } else if (midia instanceof Livro l) {
                String sqlL = "UPDATE livro SET autor = ?, paginas = ? WHERE id = ?";
                try (PreparedStatement s = conn.prepareStatement(sqlL)) {
                    s.setString(1, l.getAutor());
                    s.setInt(2, l.getPaginas());
                    s.setInt(3, midia.getId());
                    s.executeUpdate();
                }
            } else if (midia instanceof Musica m) {
                String sqlM = "UPDATE musica SET artista = ?, duracao = ? WHERE id = ?";
                try (PreparedStatement s = conn.prepareStatement(sqlM)) {
                    s.setString(1, m.getArtista());
                    s.setInt(2, m.getDuracao());
                    s.setInt(3, midia.getId());
                    s.executeUpdate();
                }
            } else if (midia instanceof Serie sObj) {
                String sqlS = "UPDATE serie SET temporadas = ? WHERE id = ?";
                try (PreparedStatement st = conn.prepareStatement(sqlS)) {
                    st.setInt(1, sObj.getTemporadas());
                    st.setInt(2, midia.getId());
                    st.executeUpdate();
                }
            } else if (midia instanceof Episodio e) {
                String sqlE = "UPDATE episodio SET temporada = ?, episodio = ?, idSerie = ? WHERE id = ?";
                try (PreparedStatement st = conn.prepareStatement(sqlE)) {
                    st.setInt(1, e.getTemporada());
                    st.setInt(2, e.getEpisodio());
                    st.setInt(3, e.getSerie().getId());
                    st.setInt(4, midia.getId());
                    st.executeUpdate();
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void excluir(int id) {
        String[] deletes = new String[] {
            "DELETE FROM episodio WHERE id = ?",
            "DELETE FROM filme WHERE id = ?",
            "DELETE FROM livro WHERE id = ?",
            "DELETE FROM musica WHERE id = ?",
            "DELETE FROM serie WHERE id = ?",
            "DELETE FROM midia WHERE id = ?"
        };

        try (Connection conn = Conexao.getConexao()) {
            for (String dsql : deletes) {
                try (PreparedStatement ps = conn.prepareStatement(dsql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    // MÉTODO buscarPorId

    public Midia buscarPorId(int id) {

        String sqlBase = "SELECT " +
            "m.id, m.nome, m.idUsuario, " +
            "u.id AS u_id, u.nome AS u_nome, u.email AS u_email, u.senha AS u_senha, " +
            "f.diretor, f.duracao AS f_duracao, " +
            "l.autor, l.paginas, " +
            "mu.artista, mu.duracao AS mu_duracao, " +
            "s.temporadas, " +
            "e.temporada, e.episodio, e.idSerie, " +
            "m_serie.nome AS nomeDaSerie, " +      // busca nome da Série PAI
            "s_serie.temporadas AS temporadasDaSerie " +
            "FROM midia m " +
            "JOIN usuario u ON m.idUsuario = u.id " +
            "LEFT JOIN filme f ON m.id = f.id " +
            "LEFT JOIN livro l ON m.id = l.id " +
            "LEFT JOIN musica mu ON m.id = mu.id " +
            "LEFT JOIN serie s ON m.id = s.id " +        // Para mídias que SÃO séries
            "LEFT JOIN episodio e ON m.id = e.id " +    // Para mídias que SÃO episódios
            "LEFT JOIN midia m_serie ON e.idSerie = m_serie.id " + // Pega o nome da Série PAI
            "LEFT JOIN serie s_serie ON e.idSerie = s_serie.id " + // Pega as temporadas da Série PAI
            "WHERE m.id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sqlBase)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // 1. Cria o Usuário
                    Usuario usuario = new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("u_nome"),
                        rs.getString("u_email"),
                        rs.getString("u_senha")
                    );

                    Midia midia = null;
                    // 2. Verifica o tipo da Mídia
                    if (rs.getString("diretor") != null) {
                        midia = new Filme(rs.getInt("id"), rs.getString("nome"), usuario, rs.getString("diretor"), rs.getInt("f_duracao"));
                    } else if (rs.getString("autor") != null) {
                        midia = new Livro(rs.getInt("id"), rs.getString("nome"), usuario, rs.getString("autor"), rs.getInt("paginas"));
                    } else if (rs.getString("artista") != null) {
                        midia = new Musica(rs.getInt("id"), rs.getString("nome"), usuario, rs.getString("artista"), rs.getInt("mu_duracao"));
                    } else if (rs.getInt("temporadas") != 0) { // É uma Série
                        midia = new Serie(rs.getInt("id"), rs.getString("nome"), usuario, rs.getInt("temporadas"));
                    
                    // CORREÇÃO APLICADA AQUI
                    } else if (rs.getInt("temporada") != 0 || rs.getInt("episodio") != 0) { // É um Episódio
                        
                        // 3. Constrói a Série-pai (agora com o nome real)
                        String nomeDaSerie = rs.getString("nomeDaSerie");
                        if (nomeDaSerie == null) {
                            nomeDaSerie = "Série Desconhecida"; // Caso o ID da série seja inválido
                        }
                        
                        Serie serie = new Serie(
                            rs.getInt("idSerie"), 
                            nomeDaSerie, // Nome da série-pai
                            usuario,     // O usuário que cadastrou o EPISÓDIO
                            rs.getInt("temporadasDaSerie") // Total de temporadas da série-pai
                        );
                        
                        midia = new Episodio(
                            rs.getInt("id"), 
                            rs.getString("nome"), // Nome do episódio
                            usuario, 
                            rs.getInt("temporada"), 
                            rs.getInt("episodio"), 
                            serie // Objeto Série-pai completo
                        );
                    }

                    return midia;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // MÉTODO listarTodas

    public List<Midia> listarTodas() {
        List<Midia> midias = new ArrayList<>();
        String sqlBase = "SELECT " +
            "m.id, m.nome, m.idUsuario, " +
            "u.id AS u_id, u.nome AS u_nome, u.email AS u_email, u.senha AS u_senha, " +
            "f.diretor, f.duracao AS f_duracao, " +
            "l.autor, l.paginas, " +
            "mu.artista, mu.duracao AS mu_duracao, " +
            "s.temporadas, " +
            "e.temporada, e.episodio, e.idSerie, " +
            "m_serie.nome AS nomeDaSerie, " +      // busca nome da Série PAI
            "s_serie.temporadas AS temporadasDaSerie " +
            "FROM midia m " +
            "JOIN usuario u ON m.idUsuario = u.id " +
            "LEFT JOIN filme f ON m.id = f.id " +
            "LEFT JOIN livro l ON m.id = l.id " +
            "LEFT JOIN musica mu ON m.id = mu.id " +
            "LEFT JOIN serie s ON m.id = s.id " +        // Para mídias que SÃO séries
            "LEFT JOIN episodio e ON m.id = e.id " +    // Para mídias que SÃO episódios
            "LEFT JOIN midia m_serie ON e.idSerie = m_serie.id " + // Pega o nome da Série PAI
            "LEFT JOIN serie s_serie ON e.idSerie = s_serie.id " + // Pega as temporadas da Série PAI
            "ORDER BY m.id"; // Bônus: ordenado para vir sempre igual

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sqlBase);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // 1. Cria o Usuário
                Usuario usuario = new Usuario(
                    rs.getInt("idUsuario"),
                    rs.getString("u_nome"),
                    rs.getString("u_email"),
                    rs.getString("u_senha")
                );

                Midia midia = null;
                // 2. Verifica o tipo da Mídia
                if (rs.getString("diretor") != null) {
                    midia = new Filme(rs.getInt("id"), rs.getString("nome"), usuario, rs.getString("diretor"), rs.getInt("f_duracao"));
                } else if (rs.getString("autor") != null) {
                    midia = new Livro(rs.getInt("id"), rs.getString("nome"), usuario, rs.getString("autor"), rs.getInt("paginas"));
                } else if (rs.getString("artista") != null) {
                    midia = new Musica(rs.getInt("id"), rs.getString("nome"), usuario, rs.getString("artista"), rs.getInt("mu_duracao"));
                } else if (rs.getInt("temporadas") != 0) { // É uma Série
                    midia = new Serie(rs.getInt("id"), rs.getString("nome"), usuario, rs.getInt("temporadas"));
                
                } else if (rs.getInt("temporada") != 0 || rs.getInt("episodio") != 0) { // É um Episódio
                    
                    // 3. Constrói a Série-pai (com nome real)
                    String nomeDaSerie = rs.getString("nomeDaSerie");
                    if (nomeDaSerie == null) {
                        nomeDaSerie = "Série Desconhecida"; // Fallback
                    }

                    Serie serie = new Serie(
                        rs.getInt("idSerie"), 
                        nomeDaSerie, // Nome da série-pai
                        usuario,     // O usuário que cadastrou o EPISÓDIO
                        rs.getInt("temporadasDaSerie")
                    );
                    
                    midia = new Episodio(
                        rs.getInt("id"), 
                        rs.getString("nome"), // Nome do episódio
                        usuario, 
                        rs.getInt("temporada"), 
                        rs.getInt("episodio"), 
                        serie // Objeto Série-pai completo
                    );
                }

                if (midia != null) { // Adiciona à lista apenas se foi um tipo reconhecido
                    midias.add(midia);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return midias;
    }
}