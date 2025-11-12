package dev.JavaLovers.MyZone.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.JavaLovers.MyZone.model.Musica;

@Repository
public interface MusicaRepository extends JpaRepository<Musica, Long> {}