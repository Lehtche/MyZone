package dev.JavaLovers.MyZone.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.JavaLovers.MyZone.model.GrupoUsuario; 
import dev.JavaLovers.MyZone.model.Midia;
import dev.JavaLovers.MyZone.model.Usuario;
import dev.JavaLovers.MyZone.repository.AvaliacaoRepository;
import dev.JavaLovers.MyZone.repository.ColecaoRepository;
import dev.JavaLovers.MyZone.repository.GrupoUsuarioRepository;
import dev.JavaLovers.MyZone.repository.MidiaRepository;
import dev.JavaLovers.MyZone.repository.UsuarioRepository;



@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final GrupoUsuarioRepository grupoUsuarioRepository; 
    private final AvaliacaoRepository avaliacaoRepository;
    private final ColecaoRepository colecaoRepository;
    private final MidiaRepository midiaRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, 
                          PasswordEncoder passwordEncoder,
                          GrupoUsuarioRepository grupoUsuarioRepository,
                          AvaliacaoRepository avaliacaoRepository,
                          ColecaoRepository colecaoRepository,    
                          MidiaRepository midiaRepository) {      
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.grupoUsuarioRepository = grupoUsuarioRepository; 
        this.avaliacaoRepository = avaliacaoRepository;
        this.colecaoRepository = colecaoRepository; 
        this.midiaRepository = midiaRepository;        
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

    @Transactional
    public void deletarConta(String email) {
        // 1. Encontrar o usuário no MySQL
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        
        // 2. Apagar todas as avaliações do MongoDB
        avaliacaoRepository.deleteByUsuarioId(usuario.getId());

        // 3. Apagar todas as coleções do MySQL
        colecaoRepository.deleteByUsuario(usuario);

        // 4. Apagar todas as mídias do usuário (usando a Stored Procedure)
        List<Midia> midiasDoUsuario = midiaRepository.findByCadastradoPor(usuario);
        for (Midia midia : midiasDoUsuario) {
            // Reutiliza a SP que já lida com a exclusão complexa de mídias
            midiaRepository.callDeletarMidia(midia.getId(), usuario.getId());
        }

        // 5. Apagar o usuário
        // A tabela 'usuario_grupo' será limpa automaticamente pelo
        // 'ON DELETE CASCADE' definido no SQL.
        usuarioRepository.delete(usuario);
    }
}