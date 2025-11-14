package dev.JavaLovers.MyZone.controller;

import dev.JavaLovers.MyZone.dto.LoginRequestDTO;
import dev.JavaLovers.MyZone.dto.UsuarioResponseDTO; 
import dev.JavaLovers.MyZone.model.GrupoUsuario; 
import dev.JavaLovers.MyZone.model.Usuario;
import dev.JavaLovers.MyZone.repository.UsuarioRepository; // <-- IMPORTAR
import dev.JavaLovers.MyZone.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Imports para criar a sessão
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority; 
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate; // <-- IMPORTAR
import java.util.ArrayList;
import java.util.List; 
import java.util.stream.Collectors; 

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository; // <-- INJETAR REPOSITÓRIO

    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) { // <-- ATUALIZAR
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository; // <-- ATUALIZAR
    }

    // --- CADASTRO (sem alteração) ---
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrarUsuario(@RequestBody Usuario novoUsuario) {
        try {
            Usuario usuarioSalvo = usuarioService.cadastrar(novoUsuario);
            UsuarioResponseDTO responseDto = new UsuarioResponseDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getDataNascimento()
            );
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); 
        }
    }

    // --- LOGIN (sem alteração) ---
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody LoginRequestDTO loginDTO, HttpServletRequest request) {
        try {
            Usuario usuario = usuarioService.login(loginDTO.getEmail(), loginDTO.getSenha());
            
            List<SimpleGrantedAuthority> authorities = usuario.getGrupos().stream()
                .map(grupo -> new SimpleGrantedAuthority(grupo.getNome()))
                .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), null, authorities );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            UsuarioResponseDTO responseDto = new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataNascimento()
            );
            
            return ResponseEntity.ok(responseDto); 
        
        } catch (Exception e) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }
    }

    // --- NOVO ENDPOINT PARA DEMONSTRAR A VIEW ---
    /**
     * Endpoint para demonstrar o uso da View 'VW_Usuarios_Publicos'.
     * Retorna uma lista de utilizadores sem dados sensíveis.
     * Acessível apenas por utilizadores logados.
     */
    @GetMapping("/publicos")
    public ResponseEntity<List<UsuarioRepository.UsuarioPublicoView>> getUsuariosPublicos() {
        // Usa o método do repositório que chama a View nativa
        List<UsuarioRepository.UsuarioPublicoView> usuarios = usuarioRepository.findAllPublic();
        return ResponseEntity.ok(usuarios);
    }
}