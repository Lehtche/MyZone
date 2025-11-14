package dev.JavaLovers.MyZone.service;

import dev.JavaLovers.MyZone.model.Avaliacao;
import dev.JavaLovers.MyZone.model.Usuario;
import dev.JavaLovers.MyZone.repository.AvaliacaoRepository;
import dev.JavaLovers.MyZone.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AvaliacaoService {

    @Autowired private AvaliacaoRepository avaliacaoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    // Busca todas as avaliações (do Mongo) de um usuário (do MySQL)
    public List<Avaliacao> listarAvaliacoesPorUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        
        return avaliacaoRepository.findByUsuarioId(usuario.getId());
    }

    // Busca todas as avaliações (do Mongo) de uma Mídia específica
    public List<Avaliacao> listarAvaliacoesPorMidia(Long midiaId) {
        return avaliacaoRepository.findByMidiaId(midiaId);
    }
}