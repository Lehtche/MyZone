package dev.JavaLovers.MyZone.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.JavaLovers.MyZone.dto.AvaliacaoDTO;
import dev.JavaLovers.MyZone.model.Avaliacao;
import dev.JavaLovers.MyZone.model.Usuario;
import dev.JavaLovers.MyZone.repository.AvaliacaoRepository;
import dev.JavaLovers.MyZone.repository.UsuarioRepository;

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

        // Deleta todas as avaliações de um usuário (quando o usuário é deletado)
    public void deletarAvaliacoesPorMidiaId(Long midiaId) {
        avaliacaoRepository.deleteByMidiaId(midiaId);
    }

    // Busca todas as avaliações (do Mongo) de uma Mídia específica
    public List<Avaliacao> listarAvaliacoesPorMidia(Long midiaId) {
        return avaliacaoRepository.findByMidiaId(midiaId);
    }
    public Avaliacao salvarOuAtualizarAvaliacao(AvaliacaoDTO dto, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Se a nota for 0 e o comentário for vazio, não faz nada
        if (dto.getNota() == 0 && (dto.getComentario() == null || dto.getComentario().trim().isEmpty())) {
            return null;
        }

        // Procura se já existe
        Optional<Avaliacao> avaliacaoOpt = avaliacaoRepository
                .findByUsuarioIdAndMidiaId(usuario.getId(), dto.getMidiaId());

        // Se existir, usa-a. Se não, cria uma nova.
        Avaliacao avaliacao = avaliacaoOpt.orElse(new Avaliacao());

        // Define (ou atualiza) os valores
        avaliacao.setUsuarioId(usuario.getId());
        avaliacao.setMidiaId(dto.getMidiaId());
        avaliacao.setNota(dto.getNota());
        avaliacao.setComentario(dto.getComentario());
        avaliacao.setDataAvaliacao(LocalDate.now()); // Atualiza a data

        return avaliacaoRepository.save(avaliacao);
    }
}