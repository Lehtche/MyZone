package dev.JavaLovers.MyZone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.JavaLovers.MyZone.dto.TmdbResponseDTO;
import dev.JavaLovers.MyZone.dto.LivroApiResponseDTO; 
import dev.JavaLovers.MyZone.dto.MusicaApiResponseDTO; 
import dev.JavaLovers.MyZone.service.MidiaService;

@RestController
@RequestMapping("/api/tmdb") 
public class TmdbController {

    @Autowired
    private MidiaService midiaService; 

    // --- Endpoint de Filmes/Séries (Sem alteração) ---
    @GetMapping("/buscar")
    public ResponseEntity<TmdbResponseDTO> buscarMidia(
            @RequestParam String query,
            @RequestParam String tipo) {
        
        TmdbResponseDTO resultado = midiaService.buscarDetalhesTmdb(query, tipo);
        
        if (resultado.getPosterUrl() == null && resultado.getAnoLancamento() == 0 && resultado.getGenero() == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(resultado);
    }

    // --- Endpoint de Livros (Sem alteração) ---
    @GetMapping("/buscar-livro")
    public ResponseEntity<LivroApiResponseDTO> buscarLivro(@RequestParam String query, @RequestParam(required = false) String autor) {
        LivroApiResponseDTO resultado = midiaService.buscarDetalhesLivro(query, autor);
        if ("N/A".equals(resultado.getAutor()) && resultado.getPosterUrl() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resultado);
    }

    /**
     * ATUALIZADO: Busca Músicas na API do Deezer
     * Agora aceita um parâmetro 'artista' opcional para refinar a busca.
     */
    @GetMapping("/buscar-musica")
    public ResponseEntity<MusicaApiResponseDTO> buscarMusica(
            @RequestParam String query,
            @RequestParam(required = false) String artista) { // <-- ARTISTA ADICIONADO
        
        MusicaApiResponseDTO resultado = midiaService.buscarDetalhesMusica(query, artista); // <-- ARTISTA ADICIONADO
         if ("N/A".equals(resultado.getArtista()) && resultado.getPosterUrl() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resultado);
    }
}