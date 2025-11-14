package dev.JavaLovers.MyZone.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.JavaLovers.MyZone.dto.FilmeDTO;
import dev.JavaLovers.MyZone.dto.LivroDTO;
import dev.JavaLovers.MyZone.dto.MusicaDTO;
import dev.JavaLovers.MyZone.dto.SerieDTO;
import dev.JavaLovers.MyZone.model.Filme;
import dev.JavaLovers.MyZone.model.Livro;
import dev.JavaLovers.MyZone.model.Midia;
import dev.JavaLovers.MyZone.model.Musica;
import dev.JavaLovers.MyZone.model.Serie;
import dev.JavaLovers.MyZone.service.MidiaService;

@RestController
@RequestMapping("/api/midias")
public class MidiaController {

    @Autowired
    private MidiaService midiaService;

    private String getEmailUsuarioLogado(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuário não autenticado.");
        }
        return principal.getName();
    }

    // --- ENDPOINTS DE CADASTRO (POST) ---
    @PostMapping("/filme")
    public ResponseEntity<Filme> addFilme(@RequestBody FilmeDTO dto, Principal principal) {
        Filme filmeSalvo = midiaService.salvarFilme(dto, getEmailUsuarioLogado(principal));
        return ResponseEntity.ok(filmeSalvo);
    }
    @PostMapping("/serie")
    public ResponseEntity<Serie> addSerie(@RequestBody SerieDTO dto, Principal principal) {
        Serie serieSalva = midiaService.salvarSerie(dto, getEmailUsuarioLogado(principal));
        return ResponseEntity.ok(serieSalva);
    }
    @PostMapping("/musica")
    public ResponseEntity<Musica> addMusica(@RequestBody MusicaDTO dto, Principal principal) {
        Musica musicaSalva = midiaService.salvarMusica(dto, getEmailUsuarioLogado(principal));
        return ResponseEntity.ok(musicaSalva);
    }
    @PostMapping("/livro")
    public ResponseEntity<Livro> addLivro(@RequestBody LivroDTO dto, Principal principal) {
        Livro livroSalvo = midiaService.salvarLivro(dto, getEmailUsuarioLogado(principal));
        return ResponseEntity.ok(livroSalvo);
    }

    // --- ENDPOINTS DE LEITURA (GET) ---
    @GetMapping("/minhas-midias")
    public ResponseEntity<List<Midia>> getMinhasMidias(Principal principal) {
        List<Midia> midias = midiaService.listarMidiasPorUsuario(getEmailUsuarioLogado(principal));
        return ResponseEntity.ok(midias);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Midia> getMidiaPorId(@PathVariable Long id, Principal principal) {
        if (principal == null) { return ResponseEntity.status(401).build(); }
        Midia midia = midiaService.getMidiaPorId(id);
        return ResponseEntity.ok(midia);
    }

    // --- NOVO ENDPOINT DE DELETE ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMidia(@PathVariable Long id, Principal principal) {
        try {
            midiaService.deletarMidia(id, getEmailUsuarioLogado(principal));
            return ResponseEntity.noContent().build(); // 204 No Content (Sucesso)
        } catch (RuntimeException e) {
            // Se o usuário não for o dono
            if (e.getMessage().contains("Acesso negado")) {
                return ResponseEntity.status(403).build(); // 403 Forbidden
            }
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}