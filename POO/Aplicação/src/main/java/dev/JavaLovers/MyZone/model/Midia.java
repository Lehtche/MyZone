package dev.JavaLovers.MyZone.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "midia")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Midia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String nome;

    @Column(columnDefinition = "TEXT")
    protected String sinopse;

    protected String posterUrl;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    protected Usuario cadastradoPor;

    public Midia() {}

    public Midia(String nome, Usuario cadastradoPor) {
        this.nome = nome;
        this.cadastradoPor = cadastradoPor;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public Usuario getCadastradoPor() { return cadastradoPor; }
    public void setCadastradoPor(Usuario cadastradoPor) { this.cadastradoPor = cadastradoPor; }

    public abstract String getTipo();
}
