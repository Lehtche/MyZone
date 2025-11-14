package dev.JavaLovers.MyZone.service;

import java.util.Optional;
import java.util.Set; // <-- IMPORTAR
import java.util.HashSet; // <-- IMPORTAR

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.JavaLovers.MyZone.model.GrupoUsuario; // <-- IMPORTAR
import dev.JavaLovers.MyZone.model.Usuario;
import dev.JavaLovers.MyZone.repository.GrupoUsuarioRepository; // <-- IMPORTAR
import dev.JavaLovers.MyZone.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final GrupoUsuarioRepository grupoUsuarioRepository; // <-- 1. ADICIONAR REPOSITÓRIO

    // --- 2. ATUALIZAR CONSTRUTOR ---
    public UsuarioService(UsuarioRepository usuarioRepository, 
                          PasswordEncoder passwordEncoder,
                          GrupoUsuarioRepository grupoUsuarioRepository) { // <-- ADICIONAR
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.grupoUsuarioRepository = grupoUsuarioRepository; // <-- ADICIONAR
    }

    // --- 3. ATUALIZAR LÓGICA DE CADASTRO ---
    public Usuario cadastrar(Usuario novoUsuario) {
        // 1. Verifica se o email já existe
        if (usuarioRepository.findByEmail(novoUsuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }

        // 2. CRIPTOGRAFA a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaCriptografada);
        
        // 3. ATRIBUI O GRUPO "ROLE_USER"
        // (O teu script SQL já deve ter inserido "ROLE_USER" na tabela grupos_usuarios)
        GrupoUsuario grupoUser = grupoUsuarioRepository.findByNome("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Erro: Grupo 'ROLE_USER' não encontrado na base de dados."));
        
        Set<GrupoUsuario> grupos = new HashSet<>();
        grupos.add(grupoUser);
        novoUsuario.setGrupos(grupos); // <-- Define o grupo para o novo utilizador
        
        // 4. Salva o usuário com a senha criptografada e o grupo
        return usuarioRepository.save(novoUsuario);
    }

    // --- LÓGICA DE LOGIN (sem alteração aqui) ---
    public Usuario login(String email, String senhaPura) {
        // 1. Busca o usuário pelo email (agora com os grupos, graças ao FetchType.EAGER)
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