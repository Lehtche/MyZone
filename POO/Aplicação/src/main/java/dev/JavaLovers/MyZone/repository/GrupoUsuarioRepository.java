package dev.JavaLovers.MyZone.repository;

import dev.JavaLovers.MyZone.model.GrupoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GrupoUsuarioRepository extends JpaRepository<GrupoUsuario, Long> {
    
    // Método para encontrar um grupo pelo seu nome
    Optional<GrupoUsuario> findByNome(String nome);
}