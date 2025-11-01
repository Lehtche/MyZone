package dev.JavaLovers.MyZone.model;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 4. IDENTITY é mais simples e robusto
    protected Long id; // 5. Long é preferível a int para IDs
    
    protected String nome;

    // 6. Relação com Usuario definida corretamente
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) // Assumindo que não pode ser nulo
    protected Usuario cadastradoPor;

    // 7. Construtor padrão (obrigatório pelo JPA)
    public Midia() {}

    // 8. Construtor de "criação" (sem o ID, que é gerado pelo banco)
    public Midia(String nome, Usuario cadastradoPor) {
        this.nome = nome;
        this.cadastradoPor = cadastradoPor;
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

    public Usuario getCadastradoPor() {
        return cadastradoPor;
    }

    public void setCadastradoPor(Usuario cadastradoPor) {
        this.cadastradoPor = cadastradoPor;
    }

    public abstract String getTipo();
}