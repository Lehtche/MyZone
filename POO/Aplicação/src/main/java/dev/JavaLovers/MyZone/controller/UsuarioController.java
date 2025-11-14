package dev.JavaLovers.MyZone.controller;

import dev.JavaLovers.MyZone.dto.LoginRequestDTO;
import dev.JavaLovers.MyZone.dto.UsuarioResponseDTO; 
import dev.JavaLovers.MyZone.model.GrupoUsuario; 
import dev.JavaLovers.MyZone.model.Usuario;
import dev.JavaLovers.MyZone.repository.UsuarioRepository; 
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

import java.security.Principal; // <-- IMPORTAR
import java.time.LocalDate; 
import java.util.ArrayList;
import java.util.List; 
import java.util.Map; // <-- IMPORTAR
import java.util.stream.Collectors; 

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository; 

    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) { 
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository; 
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
                usuarioSalvo.getDataNascimento(),
                null // fotoUrl não é retornada no cadastro
            );
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); 
        }
    }

    // --- LOGIN (Atualizado para enviar fotoUrl) ---
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

            // Envia o DTO completo, incluindo a fotoUrl
            UsuarioResponseDTO responseDto = new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataNascimento(),
                usuario.getFotoUrl() // <-- Envia a URL da foto
            );
            
            return ResponseEntity.ok(responseDto); 
        
        } catch (Exception e) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }
    }

    // --- GET USUARIOS PUBLICOS (sem alteração) ---
    @GetMapping("/publicos")
    public ResponseEntity<List<UsuarioRepository.UsuarioPublicoView>> getUsuariosPublicos() {
        List<UsuarioRepository.UsuarioPublicoView> usuarios = usuarioRepository.findAllPublic();
        return ResponseEntity.ok(usuarios);
    }
    
    // --- NOVO ENDPOINT PARA ATUALIZAR A FOTO ---
    /**
     * Atualiza a foto de perfil do utilizador logado.
     * Recebe um JSON simples: { "fotoUrl": "http://..." }
     */
    @PostMapping("/atualizar-foto")
    public ResponseEntity<Void> atualizarFoto(@RequestBody Map<String, String> payload, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        
        String fotoUrl = payload.get("fotoUrl");
        if (fotoUrl == null || fotoUrl.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Reutiliza o UsuarioService para esta lógica
            usuarioService.atualizarFoto(principal.getName(), fotoUrl);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}