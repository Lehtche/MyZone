package dev.JavaLovers.MyZone.service;

import dev.JavaLovers.MyZone.dto.*;
import dev.JavaLovers.MyZone.model.*;
import dev.JavaLovers.MyZone.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MidiaService {

    // --- Repositórios (SQL e Mongo) ---
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private FilmeRepository filmeRepository;
    @Autowired private SerieRepository serieRepository;
    @Autowired private MusicaRepository musicaRepository;
    @Autowired private LivroRepository livroRepository;
    @Autowired private MidiaRepository midiaRepository;
    @Autowired private AvaliacaoRepository avaliacaoRepository;
    
    // --- TMDb (API) ---
    @Value("${tmdb.api.key}")
    private String tmdbApiKey;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    
    // --- ATUALIZADO: Busca de Gênero e Sinopse no TMDb ---
    public TmdbResponseDTO buscarDetalhesTmdb(String query, String tipo) {
        String searchType = "movie"; 
        if ("serie".equals(tipo)) { searchType = "tv"; }
        else if ("musica".equals(tipo) || "livro".equals(tipo)) { return null; }

        try {
            // 1. CHAMDADA 1: Pesquisa a mídia para obter o ID
            String searchUrl = String.format(
                "https://api.themoviedb.org/3/search/%s?query=%s&api_key=%s&language=pt-BR",
                searchType, query.replace(" ", "+"), tmdbApiKey
            );
            String searchResponse = restTemplate.getForObject(searchUrl, String.class);
            JsonNode searchRoot = objectMapper.readTree(searchResponse);
            JsonNode results = searchRoot.path("results");
            
            if (results.isArray() && results.size() > 0) {
                JsonNode firstResult = results.get(0);
                String midiaId = firstResult.path("id").asText();

                // 2. CHAMADA 2: Busca os detalhes completos usando o ID
                String detailsUrl = String.format(
                    "https://api.themoviedb.org/3/%s/%s?api_key=%s&language=pt-BR",
                    searchType, midiaId, tmdbApiKey
                );
                String detailsResponse = restTemplate.getForObject(detailsUrl, String.class);
                JsonNode detailsRoot = objectMapper.readTree(detailsResponse);

                // 3. Extrai todos os dados
                String posterPath = detailsRoot.path("poster_path").asText(null);
                String posterUrl = (posterPath != null) ? "https://image.tmdb.org/t/p/w500" + posterPath : null;
                String sinopse = detailsRoot.path("overview").asText(null);
                
                String dataLancamento = detailsRoot.path(searchType.equals("movie") ? "release_date" : "first_air_date").asText("0000");
                int ano = 0;
                if (dataLancamento.length() >= 4) {
                    ano = Integer.parseInt(dataLancamento.substring(0, 4));
                }

                String genero = "N/A";
                JsonNode genres = detailsRoot.path("genres");
                if (genres.isArray() && genres.size() > 0) {
                    genero = genres.get(0).path("name").asText("N/A"); // Pega só o primeiro gênero
                }

                return new TmdbResponseDTO(posterUrl, sinopse, ano, genero);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar no TMDb: " + e.getMessage());
        }
        return new TmdbResponseDTO(null, null, 0, null); // Retorna vazio se não achar
    }


    private Usuario getUsuarioLogado(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    private void salvarAvaliacao(Long usuarioId, Long midiaId, int nota, String comentario) {
        // Salva se tiver nota OU um comentário
        if (nota > 0 || (comentario != null && !comentario.trim().isEmpty())) { 
            Avaliacao avaliacao = new Avaliacao();
            avaliacao.setUsuarioId(usuarioId);
            avaliacao.setMidiaId(midiaId);
            avaliacao.setNota(nota);
            avaliacao.setComentario(comentario);
            avaliacao.setDataAvaliacao(LocalDate.now());
            avaliacaoRepository.save(avaliacao); // Salva no MONGO
        }
    }

    // --- MÉTODOS DE CADASTRO (ATUALIZADOS COM SINOPSE) ---
    
    @Transactional
    public Filme salvarFilme(FilmeDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        TmdbResponseDTO tmdbData = buscarDetalhesTmdb(dto.getNome(), "filme");

        Filme filme = new Filme();
        filme.setNome(dto.getNome());
        filme.setDiretor(dto.getDiretor());
        filme.setAnoLancamento(dto.getAnoLancamento()); 
        filme.setCadastradoPor(usuario);
        if (tmdbData != null) {
            filme.setPosterUrl(tmdbData.getPosterUrl());
            filme.setSinopse(tmdbData.getSinopse()); // <-- SALVA A SINOPSE
        }
        
        Filme filmeSalvo = filmeRepository.save(filme);
        salvarAvaliacao(usuario.getId(), filmeSalvo.getId(), dto.getNota(), dto.getComentario());
        return filmeSalvo;
    }

    @Transactional
    public Serie salvarSerie(SerieDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        TmdbResponseDTO tmdbData = buscarDetalhesTmdb(dto.getNome(), "serie");

        Serie serie = new Serie();
        serie.setNome(dto.getNome());
        serie.setGenero(dto.getGenero()); // Salva o gênero
        serie.setCadastradoPor(usuario);
        
        if (tmdbData != null) {
            serie.setPosterUrl(tmdbData.getPosterUrl());
            serie.setSinopse(tmdbData.getSinopse()); // <-- SALVA A SINOPSE
            // Se o usuário não digitou um gênero, usa o do TMDb
            if (dto.getGenero() == null || dto.getGenero().isEmpty()) {
                serie.setGenero(tmdbData.getGenero());
            }
        }
        
        Serie serieSalva = serieRepository.save(serie); // MySQL
        salvarAvaliacao(usuario.getId(), serieSalva.getId(), dto.getNota(), dto.getComentario()); // Mongo
        return serieSalva;
    }
    
    // (salvarMusica e salvarLivro continuam iguais)
    @Transactional
    public Musica salvarMusica(MusicaDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Musica musica = new Musica();
        musica.setNome(dto.getNome());
        musica.setArtista(dto.getArtista());
        musica.setAlbum(dto.getAlbum());
        if(dto.getDataEstreia() != null && !dto.getDataEstreia().isEmpty()) {
            try {
                musica.setDataEstreia(LocalDate.parse(dto.getDataEstreia(), DATE_FORMATTER));
            } catch (Exception e) { /* ignora */ }
        }
        musica.setCadastradoPor(usuario);
        Musica musicaSalva = musicaRepository.save(musica); 
        salvarAvaliacao(usuario.getId(), musicaSalva.getId(), dto.getNota(), dto.getComentario()); 
        return musicaSalva;
    }
    @Transactional
    public Livro salvarLivro(LivroDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Livro livro = new Livro();
        livro.setNome(dto.getNome());
        livro.setAutor(dto.getAutor());
        livro.setGenero(dto.getGenero()); 
        livro.setCadastradoPor(usuario);
        Livro livroSalvo = livroRepository.save(livro); 
        salvarAvaliacao(usuario.getId(), livroSalvo.getId(), dto.getNota(), dto.getComentario()); 
        return livroSalvo;
    }

    // --- MÉTODOS DE LEITURA (GET) ---
    public List<Midia> listarMidiasPorUsuario(String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        return midiaRepository.findByCadastradoPor(usuario);
    }
    public Midia getMidiaPorId(Long id) {
        return midiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada."));
    }

    // --- NOVO MÉTODO DE DELETE ---
    @Transactional
    public void deletarMidia(Long midiaId, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Midia midia = getMidiaPorId(midiaId);

        // 1. Verifica se o usuário logado é o dono da mídia
        if (!midia.getCadastradoPor().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado: Você não é o dono desta mídia.");
        }

        // 2. Deleta todas as avaliações (do MongoDB)
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByMidiaId(midiaId);
        if (!avaliacoes.isEmpty()) {
            avaliacaoRepository.deleteAll(avaliacoes);
        }

        // 3. Deleta a mídia (do MySQL)
        // (E todas as relações em 'colecao' e 'episodio' devem ser deletadas em cascata - configure no @OneToMany)
        midiaRepository.delete(midia);
    }
}