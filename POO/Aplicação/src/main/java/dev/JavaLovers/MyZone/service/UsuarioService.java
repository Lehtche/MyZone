package dev.JavaLovers.MyZone.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set; 

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- IMPORTAR

import dev.JavaLovers.MyZone.model.GrupoUsuario; 
import dev.JavaLovers.MyZone.model.Usuario;
import dev.JavaLovers.MyZone.repository.GrupoUsuarioRepository; 
import dev.JavaLovers.MyZone.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final GrupoUsuarioRepository grupoUsuarioRepository; 

    public UsuarioService(UsuarioRepository usuarioRepository, 
                          PasswordEncoder passwordEncoder,
                          GrupoUsuarioRepository grupoUsuarioRepository) { 
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.grupoUsuarioRepository = grupoUsuarioRepository; 
    }

    // --- CADASTRAR (sem alteração) ---
    @Transactional // Boa prática adicionar
    public Usuario cadastrar(Usuario novoUsuario) {
        if (usuarioRepository.findByEmail(novoUsuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }
        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaCriptografada);
        
        GrupoUsuario grupoUser = grupoUsuarioRepository.findByNome("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Erro: Grupo 'ROLE_USER' não encontrado na base de dados."));
        
        Set<GrupoUsuario> grupos = new HashSet<>();
        grupos.add(grupoUser);
        novoUsuario.setGrupos(grupos);
        
        return usuarioRepository.save(novoUsuario);
    }

    // --- LOGIN (sem alteração) ---
    public Usuario login(String email, String senhaPura) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Email ou senha inválidos.");
        }
        Usuario usuario = usuarioOpt.get();

        if (passwordEncoder.matches(senhaPura, usuario.getSenha())) {
            return usuario;
        } else {
            throw new RuntimeException("Email ou senha inválidos.");
        }
    }
    
    // --- NOVO MÉTODO PARA ATUALIZAR A FOTO ---
    /**
     * Encontra um utilizador pelo email e atualiza o seu campo fotoUrl.
     * @param email Email do utilizador logado (Principal)
     * @param fotoUrl A nova URL da imagem
     */
    @Transactional
    public void atualizarFoto(String email, String fotoUrl) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado."));
        
        usuario.setFotoUrl(fotoUrl);
        usuarioRepository.save(usuario);
    }
}