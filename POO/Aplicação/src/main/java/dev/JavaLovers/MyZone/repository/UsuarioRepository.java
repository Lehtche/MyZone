package dev.JavaLovers.MyZone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- IMPORTAR
import org.springframework.stereotype.Repository;

import dev.JavaLovers.MyZone.model.Usuario;

import java.time.LocalDate;
import java.util.List; // <-- IMPORTAR
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);

    // --- ADICIONADO: MÉTODO PARA USAR A VIEW ---
    /**
     * Consulta a View VW_Usuarios_Publicos, que omite dados sensíveis.
     * Isto demonstra o uso da View SQL, como pedido nos requisitos.
     * O Spring Data JPA mapeia automaticamente o resultado para a
     * interface UsuarioPublicoView (definida abaixo).
     */
    @Query(value = "SELECT * FROM VW_Usuarios_Publicos", nativeQuery = true)
    List<UsuarioPublicoView> findAllPublic();

    // Interface de Projeção para mapear o resultado da View
    // (Os nomes dos métodos GET devem corresponder às colunas da View: id, nome, dataNascimento)
    interface UsuarioPublicoView {
        Long getId();
        String getNome();
        LocalDate getDataNascimento(); // O Spring/JPA trata da conversão
    }
}