package dev.JavaLovers.MyZone.repository;

import dev.JavaLovers.MyZone.model.Midia;
import dev.JavaLovers.MyZone.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;    
import org.springframework.data.repository.query.Param;  
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MidiaRepository extends JpaRepository<Midia, Long> {
    
    // Este método já existia
    List<Midia> findByCadastradoPor(Usuario usuario); 

    // Isto diz ao Spring para executar a Stored Procedure nativa do MySQL
    @Modifying
    @Query(value = "CALL SP_DeletarMidia(:midiaId, :usuarioId)", nativeQuery = true)
    void callDeletarMidia(@Param("midiaId") Long midiaId, @Param("usuarioId") Long usuarioId);
}