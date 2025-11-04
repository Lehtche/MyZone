package dev.JavaLovers.MyZone.dto;

// Esta classe simples serve apenas para receber o JSON do login
public class LoginRequestDTO {
    private String email;
    private String senha;

    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}