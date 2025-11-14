package dev.JavaLovers.MyZone.controller;

import dev.JavaLovers.MyZone.model.Avaliacao;
import dev.JavaLovers.MyZone.service.AvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    @Autowired 
    private AvaliacaoService avaliacaoService;

    // Endpoint para buscar todas as avaliações (do Mongo) do usuário logado
    @GetMapping("/minhas-avaliacoes")
    public ResponseEntity<List<Avaliacao>> getMinhasAvaliacoes(Principal principal) {
        if (principal == null) { 
            return ResponseEntity.status(401).build(); 
        }
        String email = principal.getName();
        List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoesPorUsuario(email);
        return ResponseEntity.ok(avaliacoes);
    }

    // NOVO: Endpoint para buscar avaliações (do Mongo) de uma mídia específica
    @GetMapping("/midia/{midiaId}")
    public ResponseEntity<List<Avaliacao>> getAvaliacoesPorMidia(@PathVariable Long midiaId, Principal principal) {
        if (principal == null) { // Garante que o usuário está logado
            return ResponseEntity.status(401).build(); 
        }
        List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoesPorMidia(midiaId);
        return ResponseEntity.ok(avaliacoes);
    }
}