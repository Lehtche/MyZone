package dev.JavaLovers.MyZone.repository;
import dev.JavaLovers.MyZone.model.Avaliacao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AvaliacaoRepository extends MongoRepository<Avaliacao, String> {
    // Busca no MongoDB por ID de Mídia (para o modal de detalhes)
    List<Avaliacao> findByMidiaId(Long midiaId);
    
    // Busca no MongoDB por ID de Usuário (para o feed principal)
    List<Avaliacao> findByUsuarioId(Long usuarioId);
}