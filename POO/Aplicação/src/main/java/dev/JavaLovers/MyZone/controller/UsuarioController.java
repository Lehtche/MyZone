package dev.JavaLovers.MyZone.controller;

import dev.JavaLovers.MyZone.dto.LoginRequestDTO; // <-- Importar DTO
import dev.JavaLovers.MyZone.model.Usuario;
// import dev.JavaLovers.MyZone.repository.UsuarioRepository; // <-- NÃO PRECISA MAIS
import dev.JavaLovers.MyZone.service.UsuarioService; // <-- IMPORTAR SERVICE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // <-- Importar
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // --- MUDANÇA: Injetar o Service ---
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // --- MUDANÇA: Endpoint de Cadastro chama o Service ---
    @PostMapping("/cadastro")
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario novoUsuario) {
        try {
            Usuario usuarioSalvo = usuarioService.cadastrar(novoUsuario);
            return ResponseEntity.ok(usuarioSalvo); // Retorna 200 OK
        } catch (Exception e) {
            // (Ex: se o email já existir)
            return ResponseEntity.badRequest().body(null); // Retorna 400
        }
    }

    // --- NOVO: Endpoint de Login ---
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequestDTO loginDTO) {
        try {
            Usuario usuario = usuarioService.login(loginDTO.getEmail(), loginDTO.getSenha());
            
            // Retorna 200 OK com os dados do usuário (sem a senha, idealmente)
            // (No futuro, você retornará um Token JWT aqui)
            return ResponseEntity.ok(usuario); 
        
        } catch (Exception e) {
            // Se o service der erro (senha/email errados)
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }
    }
}