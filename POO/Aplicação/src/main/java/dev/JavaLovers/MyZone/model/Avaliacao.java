package dev.JavaLovers.MyZone.model;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "avaliacoes") // "collection" é o nome da "tabela" no Mongo
public class Avaliacao {
    
    @Id
    private String id; // ID do Mongo é String
    
    // Guardamos apenas os IDs, não os objetos
    private Long usuarioId;
    private Long midiaId;
    
    private int nota;
    private String comentario;
    private LocalDate dataAvaliacao;

    public Avaliacao() {} // Construtor vazio

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getMidiaId() { return midiaId; }
    public void setMidiaId(Long midiaId) { this.midiaId = midiaId; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDate getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDate dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
}