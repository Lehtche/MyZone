package dev.JavaLovers.MyZone.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.JavaLovers.MyZone.dto.FilmeDTO;
import dev.JavaLovers.MyZone.dto.LivroApiResponseDTO;
import dev.JavaLovers.MyZone.dto.LivroDTO;
import dev.JavaLovers.MyZone.dto.MusicaApiResponseDTO;
import dev.JavaLovers.MyZone.dto.MusicaDTO;
import dev.JavaLovers.MyZone.dto.SerieDTO;
import dev.JavaLovers.MyZone.dto.TmdbResponseDTO;
import dev.JavaLovers.MyZone.model.Avaliacao;
import dev.JavaLovers.MyZone.model.Filme;
import dev.JavaLovers.MyZone.model.Livro;
import dev.JavaLovers.MyZone.model.Midia;
import dev.JavaLovers.MyZone.model.Musica;
import dev.JavaLovers.MyZone.model.Serie;
import dev.JavaLovers.MyZone.model.Usuario;
import dev.JavaLovers.MyZone.repository.AvaliacaoRepository;
import dev.JavaLovers.MyZone.repository.FilmeRepository;
import dev.JavaLovers.MyZone.repository.LivroRepository;
import dev.JavaLovers.MyZone.repository.MidiaRepository;
import dev.JavaLovers.MyZone.repository.MusicaRepository;
import dev.JavaLovers.MyZone.repository.SerieRepository;
import dev.JavaLovers.MyZone.repository.UsuarioRepository;

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
    
    // --- APIs ---
    @Value("${tmdb.api.key}")
    private String tmdbApiKey;
    
    @Value("${google.books.api.key}")
    private String googleBooksApiKey; 

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    
    // --- BUSCA DE FILMES/SÉRIES (TMDb) ---
    public TmdbResponseDTO buscarDetalhesTmdb(String query, String tipo) {
        String searchType = "movie"; 
        if ("serie".equals(tipo)) { searchType = "tv"; }
        else if ("musica".equals(tipo) || "livro".equals(tipo)) { 
            return new TmdbResponseDTO(null, null, 0, null, null); 
        }
        try {
            // Constrói a URL de forma segura com UriComponentsBuilder
            String searchUrl = UriComponentsBuilder.fromHttpUrl("https://api.themoviedb.org/3/search/" + searchType)
                .queryParam("query", query) // O queryParam() faz o encoding automático
                .queryParam("api_key", tmdbApiKey)
                .queryParam("language", "pt-BR")
                .toUriString();
            
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

    // --- ATUALIZADO: BUSCA DE LIVROS (Google Books API) ---
    public LivroApiResponseDTO buscarDetalhesLivro(String query, String autorQuery) {
        String autor = "N/A";
        String genero = "N/A";
        String anoLancamento = "";
        String posterUrl = null;
        String sinopse = null; 

        try {
            // --- CORREÇÃO: Constrói a query usando "intitle:" e "inauthor:" ---
            String googleQuery = "";
            if (query != null && !query.isEmpty()) {
                googleQuery = "intitle:" + query; // O UriComponentsBuilder fará o encode
            }
            
            if (autorQuery != null && !autorQuery.isEmpty()) {
                if (!googleQuery.isEmpty()) {
                    googleQuery += " "; // Espaço é o separador
                }
                googleQuery += "inauthor:" + autorQuery; // O UriComponentsBuilder fará o encode
            }
            // --- FIM DA CORREÇÃO ---
            
            UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/books/v1/volumes")
                .queryParam("q", googleQuery) 
                .queryParam("key", googleBooksApiKey) 
                .queryParam("maxResults", 1)
                .queryParam("langRestrict", "pt");
            
            String url = urlBuilder.toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            
            JsonNode items = root.path("items");

            if (items.isArray() && items.size() > 0) {
                JsonNode bookInfo = items.get(0).path("volumeInfo");
                
                // Autor
                JsonNode authors = bookInfo.path("authors");
                if (authors.isArray() && authors.size() > 0) {
                    autor = authors.get(0).asText("N/A");
                }
                
                // Gênero
                JsonNode categories = bookInfo.path("categories");
                if (categories.isArray() && categories.size() > 0) {
                    genero = categories.get(0).asText("N/A");
                }

                // Ano
                String dataPublicacao = bookInfo.path("publishedDate").asText("");
                if (dataPublicacao.length() >= 4) {
                    anoLancamento = dataPublicacao.substring(0, 4);
                }

                // Poster
                posterUrl = bookInfo.path("imageLinks").path("thumbnail").asText(null);

                // Sinopse
                sinopse = bookInfo.path("description").asText(null);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar no Google Books API: " + e.getMessage());
        }
        
        return new LivroApiResponseDTO(autor, genero, anoLancamento, posterUrl, sinopse);
    }

    // --- BUSCA DE MÚSICAS (Deezer) ---
    public MusicaApiResponseDTO buscarDetalhesMusica(String query, String artistaQuery) {
        String artista = "N/A";
        String album = "N/A";
        String posterUrl = null;
        String dataEstreia = null; 
        try {
            String deezerQuery = "track:\"" + query + "\""; 
            if (artistaQuery != null && !artistaQuery.isEmpty()) {
                deezerQuery += " artist:\"" + artistaQuery + "\"";
            }
            String searchUrl = UriComponentsBuilder.fromHttpUrl("https://api.deezer.com/search")
                .queryParam("q", deezerQuery) 
                .queryParam("limit", 1)
                .toUriString();
            String searchResponse = restTemplate.getForObject(searchUrl, String.class);
            JsonNode searchRoot = objectMapper.readTree(searchResponse);
            JsonNode data = searchRoot.path("data");
            if (data.isArray() && data.size() > 0) {
                JsonNode trackInfo = data.get(0);
                String albumId = trackInfo.path("album").path("id").asText(null);
                artista = trackInfo.path("artist").path("name").asText("N/A");
                album = trackInfo.path("album").path("title").asText("N/A");
                posterUrl = trackInfo.path("album").path("cover_medium").asText(null);
                if (albumId != null) {
                    String albumUrl = "https://api.deezer.com/album/" + albumId;
                    String albumResponse = restTemplate.getForObject(albumUrl, String.class);
                    JsonNode albumRoot = objectMapper.readTree(albumResponse);
                    dataEstreia = albumRoot.path("release_date").asText(null); 
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar no Deezer: " + e.getMessage());
        }
        return new MusicaApiResponseDTO(artista, album, posterUrl, dataEstreia);
    }


    // --- Métodos Auxiliares (getUsuarioLogado, salvarAvaliacao) ---
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

    // --- MÉTODOS DE SALVAR (CREATE) ---
    
    @Transactional
    public Filme salvarFilme(FilmeDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Filme filme = new Filme();
        filme.setNome(dto.getNome());
        filme.setCadastradoPor(usuario);

        // 1. Sempre busca da API para sinopse e poster
        TmdbResponseDTO tmdbData = buscarDetalhesTmdb(dto.getNome(), "filme"); 
        filme.setPosterUrl(tmdbData.getPosterUrl());
        filme.setSinopse(tmdbData.getSinopse());

        // 2. Prioriza os dados manuais do usuário (se existirem)
        if (dto.getDiretor() != null && !dto.getDiretor().isEmpty()) {
            filme.setDiretor(dto.getDiretor());
        } else {
            filme.setDiretor(tmdbData.getDiretor()); // Usa o da API se manual estiver vazio
        }
        
        if (dto.getAnoLancamento() != 0) {
            filme.setAnoLancamento(dto.getAnoLancamento());
        } else {
            filme.setAnoLancamento(tmdbData.getAnoLancamento()); // Usa o da API se manual for 0
        }
        
        Filme filmeSalvo = filmeRepository.save(filme);
        salvarAvaliacao(usuario.getId(), filmeSalvo.getId(), dto.getNota(), dto.getComentario());
        return filmeSalvo;
    }

    @Transactional
    public Serie salvarSerie(SerieDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Serie serie = new Serie();
        serie.setNome(dto.getNome());
        serie.setCadastradoPor(usuario);

        // 1. Sempre busca da API para sinopse e poster
        TmdbResponseDTO tmdbData = buscarDetalhesTmdb(dto.getNome(), "serie");
        serie.setPosterUrl(tmdbData.getPosterUrl());
        serie.setSinopse(tmdbData.getSinopse());

        // 2. Prioriza os dados manuais do usuário (se existirem)
        if (dto.getGenero() != null && !dto.getGenero().isEmpty()) {
            serie.setGenero(dto.getGenero());
        } else {
            serie.setGenero(tmdbData.getGenero()); // Usa o da API se manual estiver vazio
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
        musica.setCadastradoPor(usuario);
        MusicaApiResponseDTO deezerData = buscarDetalhesMusica(dto.getNome(), dto.getArtista());
        if (dto.getArtista() != null && !dto.getArtista().isEmpty()) {
            musica.setArtista(dto.getArtista());
        } else {
            musica.setArtista(deezerData.getArtista());
        }
        if (dto.getAlbum() != null && !dto.getAlbum().isEmpty()) {
            musica.setAlbum(dto.getAlbum());
        } else {
            musica.setAlbum(deezerData.getAlbum());
        }
        musica.setPosterUrl(deezerData.getPosterUrl());
        if(dto.getDataEstreia() != null && !dto.getDataEstreia().isEmpty()) {
            try {
                musica.setDataEstreia(LocalDate.parse(dto.getDataEstreia(), DATE_FORMATTER));
            } catch (Exception e) { /* ignora data mal formatada */ }
        } 
        else if (deezerData.getDataEstreia() != null && !deezerData.getDataEstreia().isEmpty()) {
             try {
                musica.setDataEstreia(LocalDate.parse(deezerData.getDataEstreia()));
            } catch (Exception e) { /* ignora data mal formatada da api */ }
        }
        Musica musicaSalva = musicaRepository.save(musica); 
        salvarAvaliacao(usuario.getId(), musicaSalva.getId(), dto.getNota(), dto.getComentario()); 
        return musicaSalva;
    }
    
    @Transactional
    public Livro salvarLivro(LivroDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Livro livro = new Livro();
        livro.setNome(dto.getNome()); 
        livro.setCadastradoPor(usuario);
        
        // Busca os detalhes (incluindo a sinopse)
        LivroApiResponseDTO booksData = buscarDetalhesLivro(dto.getNome(), dto.getAutor());
        
        if (dto.getAutor() != null && !dto.getAutor().isEmpty()) {
            livro.setAutor(dto.getAutor());
        } else {
            livro.setAutor(booksData.getAutor());
        }
        if (dto.getGenero() != null && !dto.getGenero().isEmpty()) {
            livro.setGenero(dto.getGenero());
        } else {
            livro.setGenero(booksData.getGenero());
        }
        
        // SALVA A SINOPSE E O POSTER
        livro.setSinopse(booksData.getSinopse()); 
        livro.setPosterUrl(booksData.getPosterUrl());
        
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

    // --- MÉTODO DE DELETE ---
    @Transactional
    public void deletarMidia(Long midiaId, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Midia midia = getMidiaPorId(midiaId);
        if (!midia.getCadastradoPor().getId().equals(usuario.getId())) {
             throw new RuntimeException("Acesso negado: Você não é o dono desta mídia.");
        }
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByMidiaId(midiaId);
        if (!avaliacoes.isEmpty()) {
            avaliacaoRepository.deleteAll(avaliacoes);
        }
        midiaRepository.callDeletarMidia(midiaId, usuario.getId());
    }

    // --- MÉTODOS DE ATUALIZAÇÃO (PUT) ---
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
        Filme filmeAtualizado = filmeRepository.save(filme);
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