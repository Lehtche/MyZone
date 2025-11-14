// 5. LivroRepository.java (SQL)
package dev.JavaLovers.MyZone.repository;
import dev.JavaLovers.MyZone.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {}