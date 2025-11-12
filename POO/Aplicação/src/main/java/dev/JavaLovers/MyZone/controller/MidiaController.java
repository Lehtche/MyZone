package dev.JavaLovers.MyZone.controller;

import dev.JavaLovers.MyZone.dto.*;
import dev.JavaLovers.MyZone.model.*;
import dev.JavaLovers.MyZone.service.MidiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Importar
import org.springframework.web.bind.annotation.*;
import java.security.Principal; // Importar

@RestController
@RequestMapping("/api/midias") // URL base para mídias
public class MidiaController {

    @Autowired
    private MidiaService midiaService;

    // Pega o usuário logado a partir do token/sessão
    private String getEmailUsuarioLogado(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuário não autenticado.");
        }
        return principal.getName();
    }

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
}