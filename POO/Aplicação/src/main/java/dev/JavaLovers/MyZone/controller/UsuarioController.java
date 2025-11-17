package dev.JavaLovers.MyZone.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.JavaLovers.MyZone.dto.LoginRequestDTO;
import dev.JavaLovers.MyZone.dto.UsuarioResponseDTO;
import dev.JavaLovers.MyZone.model.Usuario;
import dev.JavaLovers.MyZone.repository.UsuarioRepository;
import dev.JavaLovers.MyZone.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    // ... (Endpoint /cadastro) ...
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

    // ... (Endpoint /login) ...
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
                usuario.getDataNascimento(),
                usuario.getFotoUrl() 
            );
            
            return ResponseEntity.ok(responseDto); 
        
        } catch (Exception e) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }
    }

    // ... (Endpoint /publicos) ...
    @GetMapping("/publicos")
    public ResponseEntity<List<UsuarioRepository.UsuarioPublicoView>> getUsuariosPublicos() {
        List<UsuarioRepository.UsuarioPublicoView> usuarios = usuarioRepository.findAllPublic();
        return ResponseEntity.ok(usuarios);
    }
    
    // ... (Endpoint /atualizar-foto) ...
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
            usuarioService.atualizarFoto(principal.getName(), fotoUrl);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // --- ESTE É O LOCAL CORRETO PARA O MÉTODO DE DELETE (Passo 3) ---
    @DeleteMapping("/me")
    public ResponseEntity<Void> deletarMinhaConta(Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // Não autorizado
        }
        
        try {
            // 1. Chama o serviço para apagar tudo
            // (Agora o 'usuarioService' será encontrado!)
            usuarioService.deletarConta(principal.getName());

            // 2. Limpa o contexto de segurança (logout)
            SecurityContextHolder.clearContext();
            
            // 3. Invalida a sessão HTTP
            HttpSession session = request.getSession(false); // Pega a sessão atual sem criar uma nova
            if (session != null) {
                session.invalidate();
            }
            
            return ResponseEntity.ok().build(); // 200 OK (Sucesso)
        
        } catch (Exception e) {
            System.err.println("Erro ao deletar conta: " + e.getMessage());
            return ResponseEntity.status(500).build(); // Erro interno
        }
    }
}