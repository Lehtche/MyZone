// 4. MusicaRepository.java (SQL)
package dev.JavaLovers.MyZone.repository;
import dev.JavaLovers.MyZone.model.Musica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicaRepository extends JpaRepository<Musica, Long> {}