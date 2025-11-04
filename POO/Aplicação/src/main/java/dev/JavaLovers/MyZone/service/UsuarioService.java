package dev.JavaLovers.MyZone.service;

import dev.JavaLovers.MyZone.model.Usuario;
import dev.JavaLovers.MyZone.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- LÓGICA DE CADASTRO ATUALIZADA ---
    public Usuario cadastrar(Usuario novoUsuario) {
        // 1. Verifica se o email já existe (opcional, mas bom)
        if (usuarioRepository.findByEmail(novoUsuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }

        // 2. CRIPTOGRAFA a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaCriptografada);
        
        // 3. Salva o usuário com a senha criptografada
        return usuarioRepository.save(novoUsuario);
    }

    // --- LÓGICA DE LOGIN NOVA ---
    public Usuario login(String email, String senhaPura) {
        // 1. Busca o usuário pelo email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            // Não achou o email
            throw new RuntimeException("Email ou senha inválidos.");
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Compara a senha digitada (pura) com a senha criptografada (do banco)
        if (passwordEncoder.matches(senhaPura, usuario.getSenha())) {
            // Senha correta!
            return usuario;
        } else {
            // Senha errada
            throw new RuntimeException("Email ou senha inválidos.");
        }
    }
}