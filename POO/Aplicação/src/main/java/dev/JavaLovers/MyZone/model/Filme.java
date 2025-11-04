package dev.JavaLovers.MyZone.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "filme")
public class Filme extends Midia {
    private String diretor;
    private int anoLancamento;

    public Filme() {};

    public Filme(String nome, Usuario cadastradoPor, String diretor, int anoLancamento) {
        super(nome, cadastradoPor);
        this.diretor = diretor;
        this.anoLancamento = anoLancamento;
    }

    public String getDiretor() { 
        return diretor; 
    }
    public void setDiretor(String diretor) { 
        this.diretor = diretor; 
    }
    public int getAnoLancamento() {
        return anoLancamento;
    }
    public void setAnoLancamento(int anoLancamento) {
        this.anoLancamento = anoLancamento;
    }

    @Override
    public String getTipo() {
        return "FILME";
    }

    @Override
    public String toString() {
        return super.toString() + " | Diretor: " + diretor + " | Ano de Lançamento: " + anoLancamento;
    }
}
