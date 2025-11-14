package dev.JavaLovers.MyZone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.JavaLovers.MyZone.dto.TmdbResponseDTO;
import dev.JavaLovers.MyZone.service.MidiaService;

@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {

    @Autowired
    private MidiaService midiaService; // Reutilizamos o MidiaService

    @GetMapping("/buscar")
    public ResponseEntity<TmdbResponseDTO> buscarMidia(
            @RequestParam String query,
            @RequestParam String tipo) {
        
        // Protegido pelo Spring Security (usuário deve estar logado)
        TmdbResponseDTO resultado = midiaService.buscarDetalhesTmdb(query, tipo);
        
        if (resultado.getPosterUrl() == null && resultado.getAnoLancamento() == 0 && resultado.getGenero() == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(resultado);
    }
}