package com.myzone.model;
/*Mídia é uma classe base genérica para qualquer tipo de mídia (filmes, séries, episódios, músicas, etc.)
 * Ela contém atributos comuns a todas as mídias, como id, nome e o usuário que a cadastrou.
*/
public abstract class Midia {// representa uma mídia genérica e serve como base para outras mídias específicas
    protected int id;
    protected String nome;
    protected Usuario cadastradoPor;

    public Midia(int id, String nome, Usuario cadastradoPor) {
        this.id = id;
        this.nome = nome;
        this.cadastradoPor = cadastradoPor;
    }

    public Midia() {} // Construtor padrão vazio usado para facilitar a criação de objetos sem inicializar atributos imediatamente, inicializando depois via setters


// Getters e Setters(acessar e modificar os atributos da mídia de forma controlada)
    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
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

    public abstract String getTipo(); // Método abstrato que deve ser implementado pelas subclasses para retornar o tipo específico da mídia (obriga as subclasses a definir seu tipo)
}
