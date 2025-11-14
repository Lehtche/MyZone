package dev.JavaLovers.MyZone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.JavaLovers.MyZone.model.Colecao;
import dev.JavaLovers.MyZone.model.Midia;

@Repository
public interface ColecaoRepository extends JpaRepository<Colecao, Long> {
    // Método para encontrar todas as coleções que contêm uma mídia específica
    List<Colecao> findByMidia(Midia midia);
}