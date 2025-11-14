package dev.JavaLovers.MyZone.dto;

import java.time.LocalDate;

/**
 * DTO para representar os dados seguros da View VW_Usuarios_Publicos.
 * Não contém email ou senha.
 */
public class UsuarioPublicoDTO {
    private Long id;
    private String nome;
    private LocalDate dataNascimento;

    // Getters e Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}