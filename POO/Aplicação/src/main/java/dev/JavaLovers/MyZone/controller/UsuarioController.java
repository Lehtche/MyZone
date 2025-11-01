package dev.JavaLovers.MyZone.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.JavaLovers.MyZone.model.Usuario;
import dev.JavaLovers.MyZone.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/usuarios") // Nossa URL base para usuários
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Este é o endpoint que o seu front-end vai chamar
    @PostMapping("/cadastro")
    public Usuario cadastrarUsuario(@RequestBody Usuario novoUsuario) {
        // Por enquanto, vamos salvar direto
        // (No futuro, o SERVICE entraria aqui para validar e criptografar a senha)
        return usuarioRepository.save(novoUsuario);
    }
}