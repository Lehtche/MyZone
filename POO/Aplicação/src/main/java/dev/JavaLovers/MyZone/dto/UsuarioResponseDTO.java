package dev.JavaLovers.MyZone.dto;

import java.time.LocalDate;

// Esta classe envia dados seguros (sem senha) de volta ao front-end
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;

    // Construtor para facilitar a conversão
    public UsuarioResponseDTO(Long id, String nome, String email, LocalDate dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
    }

    // Getters (necessários para o Spring converter em JSON)
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public LocalDate getDataNascimento() { return dataNascimento; }
}