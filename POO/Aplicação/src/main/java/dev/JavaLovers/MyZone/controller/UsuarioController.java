package dev.JavaLovers.MyZone.controller;

import dev.JavaLovers.MyZone.dto.LoginRequestDTO;
import dev.JavaLovers.MyZone.dto.UsuarioResponseDTO; // <-- IMPORTAR NOVO DTO
import dev.JavaLovers.MyZone.model.Usuario;
import dev.JavaLovers.MyZone.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Imports para criar a sessão
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // --- ATUALIZADO: Retorna o DTO seguro ---
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrarUsuario(@RequestBody Usuario novoUsuario) {
        try {
            Usuario usuarioSalvo = usuarioService.cadastrar(novoUsuario);
            // Converte para o DTO de resposta
            UsuarioResponseDTO responseDto = new UsuarioResponseDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getDataNascimento()
            );
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
             // Retorna o erro exato (ex: "Email já cadastrado")
            return ResponseEntity.badRequest().body(null);
        }
    }

    // --- ATUALIZADO: Cria a sessão de login ---
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody LoginRequestDTO loginDTO, HttpServletRequest request) {
        try {
            Usuario usuario = usuarioService.login(loginDTO.getEmail(), loginDTO.getSenha());

            // --- A MÁGICA ACONTECE AQUI ---
            // 1. Cria a autenticação
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), // O 'principal' (é o email que usamos para buscar o usuário)
                null,               // Credenciais (a senha já foi verificada)
                new ArrayList<>()   // Autoridades (roles), pode deixar vazio por enquanto
            );

            // 2. Coloca o usuário no contexto de segurança do Spring
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Cria a sessão HTTP (que gera o cookie JSESSIONID)
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            // --- FIM DA MÁGICA ---

            // Converte para o DTO de resposta
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
}