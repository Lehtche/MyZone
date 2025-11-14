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

    @Autowired private ColecaoRepository colecaoRepository; // <-- 1. INJETAR O NOVO REPOSITÓRIO
    
    // --- TMDb (API) ---
    @Value("${tmdb.api.key}")
    private String tmdbApiKey;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    
    // --- ATUALIZADO: Busca de Diretor, Gênero e Sinopse no TMDb ---
    public TmdbResponseDTO buscarDetalhesTmdb(String query, String tipo) {
        String searchType = "movie"; 
        if ("serie".equals(tipo)) { searchType = "tv"; }
        else if ("musica".equals(tipo) || "livro".equals(tipo)) { 
            // Retorna DTO vazio se não for filme ou série
            return new TmdbResponseDTO(null, null, 0, null, null); 
        }

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

                // 3. Extrai dados (Poster, Sinopse, Ano, Gênero)
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
                    genero = genres.get(0).path("name").asText("N/A");
                }

                // --- 4. CORREÇÃO: CHAMADA 3 (Buscar Diretor) ---
                String diretor = "N/A";
                if ("movie".equals(searchType)) {
                    String creditsUrl = String.format(
                        "https://api.themoviedb.org/3/%s/%s/credits?api_key=%s&language=pt-BR",
                        searchType, midiaId, tmdbApiKey
                    );
                    String creditsResponse = restTemplate.getForObject(creditsUrl, String.class);
                    JsonNode creditsRoot = objectMapper.readTree(creditsResponse);
                    JsonNode crew = creditsRoot.path("crew");
                    if (crew.isArray()) {
                        for (JsonNode crewMember : crew) {
                            if ("Director".equals(crewMember.path("job").asText())) {
                                diretor = crewMember.path("name").asText("N/A");
                                break; // Encontrou o diretor, pode parar
                            }
                        }
                    }
                }
                // --- Fim da Correção ---

                // 5. Retorna o DTO completo
                return new TmdbResponseDTO(posterUrl, sinopse, ano, genero, diretor); // <-- ATUALIZADO
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar no TMDb: " + e.getMessage());
        }
        // Retorna DTO vazio se não achar
        return new TmdbResponseDTO(null, null, 0, null, null); 
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

    // --- MÉTODOS DE CADASTRO (Não precisam de alteração,
    // pois o TMDb busca os dados extras automaticamente) ---
    
    @Transactional
    public Filme salvarFilme(FilmeDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        // Busca os dados do TMDb (incluindo o diretor)
        TmdbResponseDTO tmdbData = buscarDetalhesTmdb(dto.getNome(), "filme"); 

        Filme filme = new Filme();
        filme.setNome(dto.getNome());
        filme.setCadastradoPor(usuario);
        
        // Se o utilizador não preencheu o diretor, usa o do TMDb
        if (dto.getDiretor() == null || dto.getDiretor().isEmpty()) {
            filme.setDiretor(tmdbData.getDiretor());
        } else {
            filme.setDiretor(dto.getDiretor()); // Usa o que o utilizador digitou
        }
        
        // Se o utilizador não preencheu o ano, usa o do TMDb
        if (dto.getAnoLancamento() == 0) {
            filme.setAnoLancamento(tmdbData.getAnoLancamento());
        } else {
            filme.setAnoLancamento(dto.getAnoLancamento()); // Usa o que o utilizador digitou
        }

        if (tmdbData != null) {
            filme.setPosterUrl(tmdbData.getPosterUrl());
            filme.setSinopse(tmdbData.getSinopse()); 
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
        serie.setCadastradoPor(usuario);
        
        if (tmdbData != null) {
            serie.setPosterUrl(tmdbData.getPosterUrl());
            serie.setSinopse(tmdbData.getSinopse());
            // Se o utilizador não digitou um gênero, usa o do TMDb
            if (dto.getGenero() == null || dto.getGenero().isEmpty()) {
                serie.setGenero(tmdbData.getGenero());
            } else {
                serie.setGenero(dto.getGenero()); // Usa o que o utilizador digitou
            }
        }
        
        Serie serieSalva = serieRepository.save(serie); // MySQL
        salvarAvaliacao(usuario.getId(), serieSalva.getId(), dto.getNota(), dto.getComentario()); // Mongo
        return serieSalva;
    }
    
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

    // --- 2. MÉTODO DE DELETE (TOTALMENTE ATUALIZADO) ---
    @Transactional
    public void deletarMidia(Long midiaId, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Midia midia = getMidiaPorId(midiaId); // Isso busca a entidade correta (ex: Serie, Filme)

        // 1. Verifica se o usuário logado é o dono da mídia
        if (!midia.getCadastradoPor().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado: Você não é o dono desta mídia.");
        }

        // 2. Deleta todas as avaliações (do MongoDB)
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByMidiaId(midiaId);
        if (!avaliacoes.isEmpty()) {
            avaliacaoRepository.deleteAll(avaliacoes);
        }

        // 3. CORREÇÃO (Problema da Colecao): Deleta as referências em 'colecao'
        List<Colecao> colecoes = colecaoRepository.findByMidia(midia);
        if (!colecoes.isEmpty()) {
            colecaoRepository.deleteAll(colecoes);
        }

        // 4. CORREÇÃO (Problema do Erro 1451): Deleta usando o repositório específico
        // Isto garante que a tabela "filha" (ex: 'serie') seja apagada
        // antes da tabela "pai" ('midia').
        
        if (midia instanceof Filme) {
            filmeRepository.delete((Filme) midia);
        } else if (midia instanceof Serie) {
            // A entidade Serie 
            // usa CascadeType.ALL para episodios,
            // então apagar a série também apagará os episódios.
            serieRepository.delete((Serie) midia);
        } else if (midia instanceof Musica) {
            musicaRepository.delete((Musica) midia);
        } else if (midia instanceof Livro) {
            livroRepository.delete((Livro) midia);
        } else {
            // Fallback (pode falhar, mas é uma última tentativa)
            midiaRepository.delete(midia);
        }
    }
}