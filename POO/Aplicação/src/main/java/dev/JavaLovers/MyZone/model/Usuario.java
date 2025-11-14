package dev.JavaLovers.MyZone.model;

import java.time.LocalDate;
import java.util.Set; // <-- Importado
import java.util.HashSet; // <-- Importado

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType; // <-- Importado
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn; // <-- Importado
import jakarta.persistence.JoinTable; // <-- Importado
import jakarta.persistence.ManyToMany; // <-- Importado
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private LocalDate dataNascimento;

    /**
     * Relação Muitos-para-Muitos com GrupoUsuario.
     * Define os papéis (roles) deste utilizador.
     * FetchType.EAGER: Carrega os grupos juntamente com o utilizador
     * (necessário para o Spring Security).
     * JoinTable: Especifica a tabela de ligação 'usuario_grupo'
     * que foi criada no script SQL.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_grupo", // Nome da tabela de ligação
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "grupo_id")
    )
    private Set<GrupoUsuario> grupos = new HashSet<>();

    
    // Construtores
    public Usuario() {};

    public Usuario(Long id, String nome, String email, String senha, LocalDate dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataNascimento = dataNascimento;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    // Getters e Setters para Grupos
    public Set<GrupoUsuario> getGrupos() {
        return grupos;
    }

    public void setGrupos(Set<GrupoUsuario> grupos) {
        this.grupos = grupos;
    }
}