package dev.JavaLovers.MyZone.service;

import dev.JavaLovers.MyZone.dto.*;
import dev.JavaLovers.MyZone.model.*;
import dev.JavaLovers.MyZone.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class MidiaService {

    // Injeta TODOS os repositórios 
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private FilmeRepository filmeRepository;
    @Autowired private SerieRepository serieRepository;
    @Autowired private MusicaRepository musicaRepository;
    @Autowired private LivroRepository livroRepository;
    @Autowired private AvaliacaoRepository avaliacaoRepository;

    // Busca o usuário logado pelo email
    private Usuario getUsuarioLogado(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    // Salva a avaliação
    private void salvarAvaliacao(Usuario usuario, Midia midia, int nota, String comentario) {
        if (nota > 0) { // Só salva se o usuário deu uma nota
            Avaliacao avaliacao = new Avaliacao();
            avaliacao.setUsuario(usuario);
            avaliacao.setMidia(midia);
            avaliacao.setNota(nota);
            avaliacao.setComentario(comentario);
            avaliacao.setDataAvaliacao(LocalDate.now());
            avaliacaoRepository.save(avaliacao);
        }
    }

    // --- MÉTODOS DE CADASTRO ---

    @Transactional // Garante que ou salva os dois, ou não salva nenhum
    public Filme salvarFilme(FilmeDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        
        Filme filme = new Filme();
        filme.setNome(dto.getNome());
        filme.setDiretor(dto.getDiretor());
        filme.setAnoLancamento(dto.getAnoLancamento());
        filme.setCadastradoPor(usuario);
        
        Filme filmeSalvo = filmeRepository.save(filme); // 1. Salva o filme
        
        // 2. Salva a avaliação
        salvarAvaliacao(usuario, filmeSalvo, dto.getNota(), dto.getComentario());
        
        return filmeSalvo;
    }

    @Transactional
    public Serie salvarSerie(SerieDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        
        Serie serie = new Serie();
        serie.setNome(dto.getNome());
        serie.setTemporadas(dto.getTemporadas());
        serie.setCadastradoPor(usuario);

        Serie serieSalva = serieRepository.save(serie);
        salvarAvaliacao(usuario, serieSalva, dto.getNota(), dto.getComentario());
        return serieSalva;
    }
    
    @Transactional
    public Musica salvarMusica(MusicaDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        
        Musica musica = new Musica();
        musica.setNome(dto.getNome());
        musica.setArtista(dto.getArtista());
        musica.setCadastradoPor(usuario);
        // (Adicione os outros campos aqui: album, etc.)

        Musica musicaSalva = musicaRepository.save(musica);
        salvarAvaliacao(usuario, musicaSalva, dto.getNota(), dto.getComentario());
        return musicaSalva;
    }

    @Transactional
    public Livro salvarLivro(LivroDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        
        Livro livro = new Livro();
        livro.setNome(dto.getNome());
        livro.setAutor(dto.getAutor());
        livro.setCadastradoPor(usuario);
        // (Adicione os outros campos aqui: paginas, etc.)

        Livro livroSalvo = livroRepository.save(livro);
        salvarAvaliacao(usuario, livroSalvo, dto.getNota(), dto.getComentario());
        return livroSalvo;
    }
}