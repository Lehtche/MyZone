// 2. FilmeRepository.java (SQL)
package dev.JavaLovers.MyZone.repository;
import dev.JavaLovers.MyZone.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmeRepository extends JpaRepository<Filme, Long> {}