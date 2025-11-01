package dev.JavaLovers.MyZone.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;


@Entity
@Table(name = "avaliacao")
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "midia_id", nullable = false)
    private Midia midia;
    private int nota;
    private String comentario;
    private LocalDate dataAvaliacao;

    public Avaliacao(Long id, Usuario usuario, Midia midia, int nota, String comentario, LocalDate dataAvaliacao) {
        this.id = id;
        this.usuario = usuario;
        this.midia = midia;
        this.nota = nota;
        this.comentario = comentario;
        this.dataAvaliacao = dataAvaliacao;
    }

    public Avaliacao() {}

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Midia getMidia() { return midia; }
    public void setMidia(Midia midia) { this.midia = midia; }

    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDate getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDate dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }

    @Override
    public String toString() {
        return "Avaliação {" +
                "id=" + id +
                ", usuário=" + (usuario != null ? usuario.getNome() : "null") +
                ", mídia=" + (midia != null ? midia.getNome() : "null") +
                ", nota=" + nota +
                ", comentário='" + comentario + '\'' +
                ", data=" + dataAvaliacao +
                '}';
    }
}
