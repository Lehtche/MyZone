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

    
    // --- BUSCA TMDB (Sem alteração) ---
    public TmdbResponseDTO buscarDetalhesTmdb(String query, String tipo) {
        String searchType = "movie"; 
        if ("serie".equals(tipo)) { searchType = "tv"; }
        else if ("musica".equals(tipo) || "livro".equals(tipo)) { 
            return new TmdbResponseDTO(null, null, 0, null, null); 
        }
        try {
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

                String detailsUrl = String.format(
                    "https://api.themoviedb.org/3/%s/%s?api_key=%s&language=pt-BR",
                    searchType, midiaId, tmdbApiKey
                );
                String detailsResponse = restTemplate.getForObject(detailsUrl, String.class);
                JsonNode detailsRoot = objectMapper.readTree(detailsResponse);

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
                                break; 
                            }
                        }
                    }
                }
                return new TmdbResponseDTO(posterUrl, sinopse, ano, genero, diretor);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar no TMDb: " + e.getMessage());
        }
        return new TmdbResponseDTO(null, null, 0, null, null); 
    }

    // --- MÉTODOS AUXILIARES (Sem alteração) ---
    private Usuario getUsuarioLogado(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    private void salvarAvaliacao(Long usuarioId, Long midiaId, int nota, String comentario) {
        if (nota > 0 || (comentario != null && !comentario.trim().isEmpty())) { 
            Avaliacao avaliacao = new Avaliacao();
            avaliacao.setUsuarioId(usuarioId);
            avaliacao.setMidiaId(midiaId);
            avaliacao.setNota(nota);
            avaliacao.setComentario(comentario);
            avaliacao.setDataAvaliacao(LocalDate.now());
            avaliacaoRepository.save(avaliacao); 
        }
    }

    // --- MÉTODOS DE CADASTRO (Sem alteração) ---
    
    @Transactional
    public Filme salvarFilme(FilmeDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        TmdbResponseDTO tmdbData = buscarDetalhesTmdb(dto.getNome(), "filme"); 

        Filme filme = new Filme();
        filme.setNome(dto.getNome());
        filme.setCadastradoPor(usuario);
        
        if (dto.getDiretor() == null || dto.getDiretor().isEmpty()) {
            filme.setDiretor(tmdbData.getDiretor());
        } else {
            filme.setDiretor(dto.getDiretor());
        }
        
        if (dto.getAnoLancamento() == 0) {
            filme.setAnoLancamento(tmdbData.getAnoLancamento());
        } else {
            filme.setAnoLancamento(dto.getAnoLancamento());
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
            if (dto.getGenero() == null || dto.getGenero().isEmpty()) {
                serie.setGenero(tmdbData.getGenero());
            } else {
                serie.setGenero(dto.getGenero());
            }
        }
        
        Serie serieSalva = serieRepository.save(serie);
        salvarAvaliacao(usuario.getId(), serieSalva.getId(), dto.getNota(), dto.getComentario());
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

    // --- MÉTODOS DE LEITURA (GET) (Sem alteração) ---
    public List<Midia> listarMidiasPorUsuario(String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        return midiaRepository.findByCadastradoPor(usuario);
    }
    public Midia getMidiaPorId(Long id) {
        return midiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada."));
    }

    // --- MÉTODO DE DELETE (Usa a Stored Procedure - Sem alteração) ---
    @Transactional
    public void deletarMidia(Long midiaId, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        
        // Verifica se a mídia existe
        Midia midia = getMidiaPorId(midiaId);
        
        // Verifica a posse (extra, já que a SP também verifica)
        if (!midia.getCadastradoPor().getId().equals(usuario.getId())) {
             throw new RuntimeException("Acesso negado: Você não é o dono desta mídia.");
        }

        // Apaga avaliações do MongoDB (a SP não faz isto)
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByMidiaId(midiaId);
        if (!avaliacoes.isEmpty()) {
            avaliacaoRepository.deleteAll(avaliacoes);
        }

        // Chama a Stored Procedure do MySQL para apagar a mídia e suas dependências
        midiaRepository.callDeletarMidia(midiaId, usuario.getId());
    }

    // --- NOVO: MÉTODOS DE ATUALIZAÇÃO (PUT) ---
    
    /**
     * Método genérico privado para verificar posse antes de atualizar.
     */
    private Midia verificarPosse(Long midiaId, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Midia midia = getMidiaPorId(midiaId);
        if (!midia.getCadastradoPor().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado: Você não é o dono desta mídia.");
        }
        return midia;
    }

    @Transactional
    public Filme atualizarFilme(Long id, FilmeDTO dto, String emailUsuario) {
        Midia midia = verificarPosse(id, emailUsuario);
        if (!(midia instanceof Filme)) {
            throw new RuntimeException("Tipo de mídia incorreto.");
        }
        
        Filme filme = (Filme) midia;
        filme.setNome(dto.getNome());
        filme.setDiretor(dto.getDiretor());
        filme.setAnoLancamento(dto.getAnoLancamento());
        // Não atualizamos poster/sinopse aqui (pode ser adicionado se necessário)

        Filme filmeAtualizado = filmeRepository.save(filme);
        
        // Salva uma *nova* avaliação (ou atualiza uma existente - lógica a definir)
        // Por enquanto, apenas salva uma nova se nota ou comentário forem enviados
        salvarAvaliacao(filme.getCadastradoPor().getId(), filme.getId(), dto.getNota(), dto.getComentario());
        
        return filmeAtualizado;
    }

    @Transactional
    public Serie atualizarSerie(Long id, SerieDTO dto, String emailUsuario) {
        Midia midia = verificarPosse(id, emailUsuario);
        if (!(midia instanceof Serie)) {
            throw new RuntimeException("Tipo de mídia incorreto.");
        }

        Serie serie = (Serie) midia;
        serie.setNome(dto.getNome());
        serie.setGenero(dto.getGenero());

        Serie serieAtualizada = serieRepository.save(serie);
        salvarAvaliacao(serie.getCadastradoPor().getId(), serie.getId(), dto.getNota(), dto.getComentario());
        return serieAtualizada;
    }

    @Transactional
    public Musica atualizarMusica(Long id, MusicaDTO dto, String emailUsuario) {
        Midia midia = verificarPosse(id, emailUsuario);
        if (!(midia instanceof Musica)) {
            throw new RuntimeException("Tipo de mídia incorreto.");
        }

        Musica musica = (Musica) midia;
        musica.setNome(dto.getNome());
        musica.setArtista(dto.getArtista());
        musica.setAlbum(dto.getAlbum());
        if(dto.getDataEstreia() != null && !dto.getDataEstreia().isEmpty()) {
            try {
                musica.setDataEstreia(LocalDate.parse(dto.getDataEstreia(), DATE_FORMATTER));
            } catch (Exception e) { /* ignora */ }
        }

        Musica musicaAtualizada = musicaRepository.save(musica);
        salvarAvaliacao(musica.getCadastradoPor().getId(), musica.getId(), dto.getNota(), dto.getComentario());
        return musicaAtualizada;
    }

    @Transactional
    public Livro atualizarLivro(Long id, LivroDTO dto, String emailUsuario) {
        Midia midia = verificarPosse(id, emailUsuario);
        if (!(midia instanceof Livro)) {
            throw new RuntimeException("Tipo de mídia incorreto.");
        }

        Livro livro = (Livro) midia;
        livro.setNome(dto.getNome());
        livro.setAutor(dto.getAutor());
        livro.setGenero(dto.getGenero());

        Livro livroAtualizado = livroRepository.save(livro);
        salvarAvaliacao(livro.getCadastradoPor().getId(), livro.getId(), dto.getNota(), dto.getComentario());
        return livroAtualizado;
    }
}