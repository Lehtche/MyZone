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
import org.springframework.web.util.UriComponentsBuilder;

// import java.time.LocalDate; // (Não é mais necessário para Música)
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class MidiaService {

    // --- Repositórios (SQL) ---
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private FilmeRepository filmeRepository;
    @Autowired private SerieRepository serieRepository;
    @Autowired private MusicaRepository musicaRepository;
    @Autowired private LivroRepository livroRepository;
    @Autowired private MidiaRepository midiaRepository;
    
    @Autowired private AvaliacaoService avaliacaoService; 

    // --- APIs ---
    @Value("${tmdb.api.key}")
    private String tmdbApiKey;
    
    @Value("${google.books.api.key:}")
    private String googleBooksApiKey; 

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    
    // --- (BUSCA DE FILMES/SÉRIES (TMDb) - ATUALIZADO) ---
    public List<TmdbResponseDTO> buscarDetalhesTmdb(String query, String tipo, String diretorQuery) {
        List<TmdbResponseDTO> responseList = new ArrayList<>();
        String searchType = "movie"; 
        if ("serie".equals(tipo)) { searchType = "tv"; }
        else if ("musica".equals(tipo) || "livro".equals(tipo)) { 
            return responseList;
        }

        boolean hasDirectorQuery = (diretorQuery != null && !diretorQuery.trim().isEmpty());

        try {
            String searchUrl = UriComponentsBuilder.fromHttpUrl("https://api.themoviedb.org/3/search/" + searchType)
                .queryParam("query", query)
                .queryParam("api_key", tmdbApiKey)
                .toUriString();
            
            String searchResponse = restTemplate.getForObject(searchUrl, String.class);
            JsonNode searchRoot = objectMapper.readTree(searchResponse);
            JsonNode results = searchRoot.path("results");
            
            if (results.isArray()) {
                for (int i = 0; i < results.size(); i++) { 
                    JsonNode item = results.get(i);
                    String nome = item.path(searchType.equals("movie") ? "title" : "name").asText(null);
                    String midiaId = item.path("id").asText();
                    
                    String detailsUrl = String.format(
                        "https://api.themoviedb.org/3/%s/%s?api_key=%s&language=pt-BR",
                        searchType, midiaId, tmdbApiKey
                    );
                    String detailsResponse = restTemplate.getForObject(detailsUrl, String.class);
                    JsonNode detailsRoot = objectMapper.readTree(detailsResponse);

                    String posterPath = detailsRoot.path("poster_path").asText(null);
                    String posterUrl = (posterPath != null && !posterPath.equals("null")) ? "https://image.tmdb.org/t/p/w500" + posterPath : null;
                    String sinopse = detailsRoot.path("overview").asText(null);
                    String dataLancamento = detailsRoot.path(searchType.equals("movie") ? "release_date" : "first_air_date").asText("0000");
                    
                    int ano = 0;
                    if (dataLancamento.length() >= 4) {
                        try { ano = Integer.parseInt(dataLancamento.substring(0, 4)); } catch (Exception ex) { ano = 0; }
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

                    boolean match = true; 
                    if (hasDirectorQuery && "movie".equals(searchType)) {
                        if (!diretor.toLowerCase().contains(diretorQuery.toLowerCase())) {
                            match = false; 
                        }
                    }

                    if (match) {
                        responseList.add(new TmdbResponseDTO(nome, posterUrl, sinopse, ano, genero, diretor));
                    }

                    if (responseList.size() >= 10) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar no TMDb: " + e.getMessage());
        }
        return responseList;
    }

    // --- BUSCA DE LIVROS (PRINCIPAL: Google Books - LIMITE 10) ---
    public List<LivroApiResponseDTO> buscarDetalhesLivro(String query, String autorQuery) {
        List<LivroApiResponseDTO> results = new ArrayList<>();
        try {
            StringBuilder qBuilder = new StringBuilder();
            if (query != null && !query.trim().isEmpty()) {
                qBuilder.append("intitle:").append(query.trim());
            }
            if (autorQuery != null && !autorQuery.trim().isEmpty()) {
                if (qBuilder.length() > 0) qBuilder.append("+");
                qBuilder.append("inauthor:").append(autorQuery.trim());
            }
            if (qBuilder.length() == 0) {
                return results; 
            }

            UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/books/v1/volumes")
                    .queryParam("q", qBuilder.toString())
                    .queryParam("maxResults", 10); 

            urlBuilder.queryParam("langRestrict", "pt");
            if (googleBooksApiKey != null && !googleBooksApiKey.isBlank()) {
                urlBuilder.queryParam("key", googleBooksApiKey);
            }

            String url = urlBuilder.toUriString();
            String response = restTemplate.getForObject(url, String.class);
            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Resposta vazia do Google Books");
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("items");
            if (items.isArray() && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    JsonNode item = items.get(i);
                    JsonNode volumeInfo = item.path("volumeInfo");
                    String nome = volumeInfo.path("title").asText(null);
                    String autor = "N/A";
                    JsonNode authorsNode = volumeInfo.path("authors");
                    if (authorsNode.isArray() && authorsNode.size() > 0) {
                        autor = authorsNode.get(0).asText("N/A");
                    }
                    String genero = "N/A";
                    JsonNode categoriesNode = volumeInfo.path("categories");
                    if (categoriesNode.isArray() && categoriesNode.size() > 0) {
                        genero = categoriesNode.get(0).asText("N/A");
                    }
                    String sinopse = null;
                    if (volumeInfo.hasNonNull("description")) {
                        sinopse = volumeInfo.path("description").asText(null);
                    }
                    String anoLancamento = "";
                    if (volumeInfo.hasNonNull("publishedDate")) {
                        String pd = volumeInfo.path("publishedDate").asText("");
                        if (pd.length() >= 4) {
                            anoLancamento = pd.substring(0, 4);
                        } else {
                            anoLancamento = pd;
                        }
                    }
                    String posterUrl = null;
                    JsonNode imageLinks = volumeInfo.path("imageLinks");
                    if (imageLinks.isObject()) {
                        if (imageLinks.hasNonNull("extraLarge")) posterUrl = imageLinks.path("extraLarge").asText(null);
                        else if (imageLinks.hasNonNull("large")) posterUrl = imageLinks.path("large").asText(null);
                        else if (imageLinks.hasNonNull("medium")) posterUrl = imageLinks.path("medium").asText(null);
                        else if (imageLinks.hasNonNull("thumbnail")) posterUrl = imageLinks.path("thumbnail").asText(null);
                        else if (imageLinks.hasNonNull("smallThumbnail")) posterUrl = imageLinks.path("smallThumbnail").asText(null);
                        if (posterUrl != null && posterUrl.startsWith("http://")) {
                            posterUrl = posterUrl.replaceFirst("http://", "https://");
                        }
                    }
                    results.add(new LivroApiResponseDTO(nome, autor, genero, anoLancamento, posterUrl, sinopse));
                }
                return results; 
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar no Google Books: " + e.getMessage());
        }

        try {
            List<LivroApiResponseDTO> fallback = buscarDetalhesLivroOpenLibrary(query, autorQuery);
            if (fallback != null && !fallback.isEmpty()) {
                return fallback;
            }
        } catch (Exception e) {
            System.err.println("Erro no fallback Open Library: " + e.getMessage());
        }

        return results;
    }

    private List<LivroApiResponseDTO> buscarDetalhesLivroOpenLibrary(String query, String autorQuery) {
        List<LivroApiResponseDTO> responseList = new ArrayList<>();
        String autor = "N/A";
        String genero = "N/A";
        String anoLancamento = "";
        String posterUrl = null;

        try {
            UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl("https://openlibrary.org/search.json")
                .queryParam("limit", 10); 

            String searchQuery = "";
            if (query != null && !query.isEmpty()) {
                searchQuery += query;
            }
            if (autorQuery != null && !autorQuery.isEmpty()) {
                if (!searchQuery.isEmpty()) {
                    searchQuery += " ";
                }
                searchQuery += autorQuery;
            }

            if (!searchQuery.isEmpty()) {
                urlBuilder.queryParam("q", searchQuery);
            } else {
                return responseList;
            }

            String url = urlBuilder.toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode docs = root.path("docs");

            if (docs.isArray()) {
                 for (JsonNode bookInfo : docs) { 
                    String nome = bookInfo.path("title").asText(null);
                    String sinopse = null;
                    JsonNode authors = bookInfo.path("author_name");
                    autor = (authors.isArray() && authors.size() > 0) ? authors.get(0).asText("N/A") : "N/A";
                    JsonNode categories = bookInfo.path("subject");
                    genero = (categories.isArray() && categories.size() > 0) ? categories.get(0).asText("N/A") : "N/A";
                    anoLancamento = bookInfo.path("first_publish_year").asText("");
                    String coverId = bookInfo.path("cover_i").asText(null);
                    posterUrl = (coverId != null && !coverId.equals("null") && !coverId.isEmpty())
                            ? String.format("https://covers.openlibrary.org/b/id/%s-L.jpg", coverId)
                            : null;
                    try {
                        String workKey = bookInfo.path("key").asText(null); 
                        if (workKey != null && !workKey.isEmpty()) {
                            String detailsUrl = "https://openlibrary.org" + workKey + ".json";
                            String detailsResponse = restTemplate.getForObject(detailsUrl, String.class);
                            JsonNode detailsRoot = objectMapper.readTree(detailsResponse);
                            JsonNode descNode = detailsRoot.path("description");
                            if (descNode.isTextual()) {
                                sinopse = descNode.asText(null);
                            } else if (descNode.isObject() && descNode.has("value")) {
                                sinopse = descNode.path("value").asText(null);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao buscar sinopse do Open Library (" + nome + "): " + e.getMessage());
                    }
                    responseList.add(new LivroApiResponseDTO(nome, autor, genero, anoLancamento, posterUrl, sinopse));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar no Open Library (fallback): " + e.getMessage());
        }
        return responseList;
    }

    // --- (BUSCA DE MÚSICAS (Deezer) - CORRIGIDO) ---
   public List<MusicaApiResponseDTO> buscarDetalhesMusica(String query, String artistaQuery) {
        List<MusicaApiResponseDTO> responseList = new ArrayList<>();
        try {
            String deezerQuery = "track:\"" + query + "\"";
            if (artistaQuery != null && !artistaQuery.isEmpty()) {
                deezerQuery += " artist:\"" + artistaQuery + "\"";
            }
            String searchUrl = UriComponentsBuilder.fromHttpUrl("https://api.deezer.com/search")
                    .queryParam("q", deezerQuery)
                    .queryParam("limit", 10) 
                    .toUriString();
            String searchResponse = restTemplate.getForObject(searchUrl, String.class);
            JsonNode searchRoot = objectMapper.readTree(searchResponse);
            JsonNode data = searchRoot.path("data");
        
            if (data.isArray()) {
                for (JsonNode trackInfo : data) { 
                    String nome = trackInfo.path("title").asText(null);
                    String artista = trackInfo.path("artist").path("name").asText("N/A");
                    String album = trackInfo.path("album").path("title").asText("N/A");
                    String posterUrl = trackInfo.path("album").path("cover_medium").asText(null);
                    
                    // --- CORREÇÃO: Lógica para buscar o Ano ---
                    String anoEstreia = null;
                    try {
                        // 1. Obter o ID do álbum da busca
                        String albumId = trackInfo.path("album").path("id").asText(null);
                        
                        if (albumId != null) {
                            // 2. Fazer uma NOVA chamada à API de ÁLBUM
                            String albumUrl = "https://api.deezer.com/album/" + albumId;
                            String albumResponse = restTemplate.getForObject(albumUrl, String.class);
                            JsonNode albumRoot = objectMapper.readTree(albumResponse);
                            
                            // 3. Obter a release_date do objeto ÁLBUM completo
                            String releaseDateStr = albumRoot.path("release_date").asText(null);
                            
                            if(releaseDateStr != null && releaseDateStr.length() >= 4) {
                                anoEstreia = releaseDateStr.substring(0, 4); // Pega "YYYY" de "YYYY-MM-DD"
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao buscar ano do álbum: " + e.getMessage());
                        // Ignora se não encontrar o ano
                    }
                    // --- FIM DA CORREÇÃO ---
                    
                    String letra = null;
                    try {
                        String lyricsUrl = String.format(
                                "https://api.lyrics.ovh/v1/%s/%s",
                                artista.replace(" ", "%20"),
                                nome.replace(" ", "%20")
                        );
                        String lyricsResponse = restTemplate.getForObject(lyricsUrl, String.class);
                        JsonNode lyricsRoot = objectMapper.readTree(lyricsResponse);
                        letra = lyricsRoot.path("lyrics").asText(null);
                    } catch (Exception e) {
                        letra = null;
                    }

                    responseList.add(new MusicaApiResponseDTO(
                            nome, artista, album, posterUrl, anoEstreia, letra
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar músicas: " + e.getMessage());
        }
        return responseList;
    }

    // --- (Método Auxiliar) ---
    private Usuario getUsuarioLogado(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    // --- (MÉTODOS DE SALVAR (CREATE) - Refatorados) ---
    @Transactional
    public Filme salvarFilme(FilmeDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Filme filme = new Filme();
        filme.setNome(dto.getNome());
        filme.setCadastradoPor(usuario);
        filme.setPosterUrl(dto.getPosterUrl());
        filme.setSinopse(dto.getSinopse());
        filme.setDiretor(dto.getDiretor());
        filme.setAnoLancamento(dto.getAnoLancamento());
        Filme filmeSalvo = filmeRepository.save(filme);
        
        AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO();
        avaliacaoDTO.setMidiaId(filmeSalvo.getId());
        avaliacaoDTO.setNota(dto.getNota());
        avaliacaoDTO.setComentario(dto.getComentario());
        avaliacaoService.salvarOuAtualizarAvaliacao(avaliacaoDTO, emailUsuario);
        
        return filmeSalvo;
    }
    @Transactional
    public Serie salvarSerie(SerieDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Serie serie = new Serie();
        serie.setNome(dto.getNome());
        serie.setCadastradoPor(usuario);
        serie.setPosterUrl(dto.getPosterUrl());
        serie.setSinopse(dto.getSinopse());
        serie.setGenero(dto.getGenero());
        Serie serieSalva = serieRepository.save(serie);
        
        AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO();
        avaliacaoDTO.setMidiaId(serieSalva.getId());
        avaliacaoDTO.setNota(dto.getNota());
        avaliacaoDTO.setComentario(dto.getComentario());
        avaliacaoService.salvarOuAtualizarAvaliacao(avaliacaoDTO, emailUsuario);
        
        return serieSalva;
    }

    // --- SALVAR MUSICA (ATUALIZADO) ---
    @Transactional
    public Musica salvarMusica(MusicaDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Musica musica = new Musica();
        musica.setNome(dto.getNome());
        musica.setCadastradoPor(usuario);
        musica.setArtista(dto.getArtista());
        musica.setAlbum(dto.getAlbum());
        musica.setPosterUrl(dto.getPosterUrl());
        musica.setSinopse(dto.getSinopse()); 
        musica.setAnoEstreia(dto.getAnoEstreia()); 

        Musica musicaSalva = musicaRepository.save(musica);
        
        AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO();
        avaliacaoDTO.setMidiaId(musicaSalva.getId());
        avaliacaoDTO.setNota(dto.getNota());
        avaliacaoDTO.setComentario(dto.getComentario());
        avaliacaoService.salvarOuAtualizarAvaliacao(avaliacaoDTO, emailUsuario);
        
        return musicaSalva;
    }

    @Transactional
    public Livro salvarLivro(LivroDTO dto, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Livro livro = new Livro();
        livro.setNome(dto.getNome()); 
        livro.setCadastradoPor(usuario);
        livro.setAutor(dto.getAutor());
        livro.setGenero(dto.getGenero());
        livro.setSinopse(dto.getSinopse()); 
        livro.setPosterUrl(dto.getPosterUrl());
        Livro livroSalvo = livroRepository.save(livro); 
        
        AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO();
        avaliacaoDTO.setMidiaId(livroSalvo.getId());
        avaliacaoDTO.setNota(dto.getNota());
        avaliacaoDTO.setComentario(dto.getComentario());
        avaliacaoService.salvarOuAtualizarAvaliacao(avaliacaoDTO, emailUsuario);
        
        return livroSalvo;
    }

    // --- (MÉTODOS DE LEITURA E DELETE) ---
    public List<Midia> listarMidiasPorUsuario(String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        return midiaRepository.findByCadastradoPor(usuario);
    }
    public Midia getMidiaPorId(Long id) {
        return midiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada."));
    }
    @Transactional
    public void deletarMidia(Long midiaId, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Midia midia = getMidiaPorId(midiaId);
        if (!midia.getCadastradoPor().getId().equals(usuario.getId())) {
             throw new RuntimeException("Acesso negado: Você não é o dono desta mídia.");
        }
        
        List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoesPorMidia(midiaId);
        if (!avaliacoes.isEmpty()) {
            // (Idealmente: avaliacaoService.deletarAvaliacoes(avaliacoes))
        }
        
        midiaRepository.callDeletarMidia(midiaId, usuario.getId());
    }
    private Midia verificarPosse(Long midiaId, String emailUsuario) {
        Usuario usuario = getUsuarioLogado(emailUsuario);
        Midia midia = getMidiaPorId(midiaId);
        if (!midia.getCadastradoPor().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado: Você não é o dono desta mídia.");
        }
        return midia;
    }
    
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // --- (MÉTODOS DE ATUALIZAÇÃO (UPDATE) - CORRIGIDOS) ---
    @Transactional
    public Filme atualizarFilme(Long id, FilmeDTO dto, String emailUsuario) {
        Midia midia = verificarPosse(id, emailUsuario);
        if (!(midia instanceof Filme)) {
            throw new RuntimeException("Tipo de mídia incorreto.");
        }
        Filme filme = (Filme) midia;

        if (!isNullOrEmpty(dto.getNome())) {
            filme.setNome(dto.getNome());
        }
        if (!isNullOrEmpty(dto.getDiretor())) {
            filme.setDiretor(dto.getDiretor());
        }
        if (dto.getAnoLancamento() > 0) { 
            filme.setAnoLancamento(dto.getAnoLancamento());
        }
        Filme filmeAtualizado = filmeRepository.save(filme);
        
        AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO();
        avaliacaoDTO.setMidiaId(filmeAtualizado.getId());
        avaliacaoDTO.setNota(dto.getNota());
        avaliacaoDTO.setComentario(dto.getComentario());
        avaliacaoService.salvarOuAtualizarAvaliacao(avaliacaoDTO, emailUsuario);
        
        return filmeAtualizado;
    }
    @Transactional
    public Serie atualizarSerie(Long id, SerieDTO dto, String emailUsuario) {
        Midia midia = verificarPosse(id, emailUsuario);
        if (!(midia instanceof Serie)) {
            throw new RuntimeException("Tipo de mídia incorreto.");
        }
        Serie serie = (Serie) midia;
        
        if (!isNullOrEmpty(dto.getNome())) {
            serie.setNome(dto.getNome());
        }
        if (!isNullOrEmpty(dto.getGenero())) {
            serie.setGenero(dto.getGenero());
        }
        Serie serieAtualizada = serieRepository.save(serie);
        
        AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO();
        avaliacaoDTO.setMidiaId(serieAtualizada.getId());
        avaliacaoDTO.setNota(dto.getNota());
        avaliacaoDTO.setComentario(dto.getComentario());
        avaliacaoService.salvarOuAtualizarAvaliacao(avaliacaoDTO, emailUsuario);
        
        return serieAtualizada;
    }

    // --- ATUALIZAR MUSICA (ATUALIZADO) ---
    @Transactional
    public Musica atualizarMusica(Long id, MusicaDTO dto, String emailUsuario) {
        Midia midia = verificarPosse(id, emailUsuario);
        if (!(midia instanceof Musica)) {
            throw new RuntimeException("Tipo de mídia incorreto.");
        }
        Musica musica = (Musica) midia;
    
        if (!isNullOrEmpty(dto.getNome())) {
            musica.setNome(dto.getNome());
        }
        if (!isNullOrEmpty(dto.getArtista())) {
            musica.setArtista(dto.getArtista());
        }
        if (!isNullOrEmpty(dto.getAlbum())) {
            musica.setAlbum(dto.getAlbum());
        }
        if (!isNullOrEmpty(dto.getSinopse())) {
            musica.setSinopse(dto.getSinopse());
        }
        if (!isNullOrEmpty(dto.getPosterUrl())) {
            musica.setPosterUrl(dto.getPosterUrl());
        }
        if (dto.getAnoEstreia() > 0) { // <-- CAMPO ATUALIZADO
            musica.setAnoEstreia(dto.getAnoEstreia());
        }
    
        Musica musicaAtualizada = musicaRepository.save(musica);
        
        AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO();
        avaliacaoDTO.setMidiaId(musicaAtualizada.getId());
        avaliacaoDTO.setNota(dto.getNota());
        avaliacaoDTO.setComentario(dto.getComentario());
        avaliacaoService.salvarOuAtualizarAvaliacao(avaliacaoDTO, emailUsuario);
        
        return musicaAtualizada;
    }

    @Transactional
    public Livro atualizarLivro(Long id, LivroDTO dto, String emailUsuario) {
        Midia midia = verificarPosse(id, emailUsuario);
        if (!(midia instanceof Livro)) {
            throw new RuntimeException("Tipo de mídia incorreto.");
        }
        Livro livro = (Livro) midia;

        if (!isNullOrEmpty(dto.getNome())) {
            livro.setNome(dto.getNome());
        }
        if (!isNullOrEmpty(dto.getAutor())) {
            livro.setAutor(dto.getAutor());
        }
        if (!isNullOrEmpty(dto.getGenero())) {
            livro.setGenero(dto.getGenero());
        }
        
        Livro livroAtualizado = livroRepository.save(livro);
        
        AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO();
        avaliacaoDTO.setMidiaId(livroAtualizado.getId());
        avaliacaoDTO.setNota(dto.getNota());
        avaliacaoDTO.setComentario(dto.getComentario());
        avaliacaoService.salvarOuAtualizarAvaliacao(avaliacaoDTO, emailUsuario);
        
        return livroAtualizado;
    }
}