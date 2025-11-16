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
import java.util.List; 

@RestController
@RequestMapping("/api/tmdb") 
public class TmdbController {

    @Autowired
    private MidiaService midiaService; 

    // --- ATUALIZADO: Endpoint de Filmes/Séries (aceita diretor) ---
    @GetMapping("/buscar")
    public ResponseEntity<List<TmdbResponseDTO>> buscarMidia( 
            @RequestParam String query,
            @RequestParam String tipo,
            @RequestParam(required = false) String diretorQuery) { // <-- PARÂMETRO ADICIONADO
        
        // Passa o diretor para o serviço
        List<TmdbResponseDTO> resultado = midiaService.buscarDetalhesTmdb(query, tipo, diretorQuery); 
        
        if (resultado.isEmpty()) { 
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(resultado);
    }

    // --- ATUALIZADO: Endpoint de Livros (limite 10) ---
    @GetMapping("/buscar-livro")
    public ResponseEntity<List<LivroApiResponseDTO>> buscarLivro( 
            @RequestParam String query, 
            @RequestParam(required = false) String autor) {
        
        List<LivroApiResponseDTO> resultado = midiaService.buscarDetalhesLivro(query, autor); 
        
        if (resultado.isEmpty()) { 
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resultado);
    }

    // --- ATUALIZADO: Endpoint de Músicas (limite 10) ---
    @GetMapping("/buscar-musica")
    public ResponseEntity<List<MusicaApiResponseDTO>> buscarMusica( 
            @RequestParam String query,
            @RequestParam(required = false) String artista) {
        
        List<MusicaApiResponseDTO> resultado = midiaService.buscarDetalhesMusica(query, artista); 
         
         if (resultado.isEmpty()) { 
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resultado);
    }
}