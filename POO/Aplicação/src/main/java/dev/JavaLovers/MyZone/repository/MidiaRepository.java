package dev.JavaLovers.MyZone.repository;

import dev.JavaLovers.MyZone.model.Midia;
import dev.JavaLovers.MyZone.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MidiaRepository extends JpaRepository<Midia, Long> {
    // Busca no MySQL todas as mídias (Filme, Serie, etc.) de um usuário
    List<Midia> findByCadastradoPor(Usuario usuario); 
}