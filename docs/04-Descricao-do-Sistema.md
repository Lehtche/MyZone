# 4. Descrição do Sistema

[Anterior: Metodologia](./03-Metodologia.md) | [Próximo: Banco SQL](./05-Banco-Relacional-MySQL.md)

O MyZone é uma aplicação web desenvolvida com o objetivo de permitir que os usuários registrem, organizem e acompanhem mídias consumidas no dia a dia, como filmes, séries, músicas e livros. O sistema foi projetado com uma arquitetura híbrida, integrando um banco de dados relacional (MySQL) para informações estruturadas e um banco NoSQL (MongoDB) para dados de avaliação.

### 4.1 Funcionalidades do Sistema

#### 4.1.1 Cadastro e Autenticação de Usuários
* Registro de novos usuários, com verificação de e-mail único.
* Senhas armazenadas de forma segura utilizando hashing (BCrypt).
* Login baseado nas tabelas `usuarios` e `grupos_usuarios` com validação via Spring Security.

#### 4.1.2 CRUD de Mídias
* **Criar:** O usuário pode adicionar mídias (filme, série, música, livro) por meio de formulários.
* **Ler:** As mídias cadastradas são exibidas no feed do usuário.
* **Atualizar:** Cada mídia pode ser editada.
* **Excluir:** A remoção de mídias é realizada através da stored procedure `SP_DeletarMidia`.

#### 4.1.3 Avaliações (MongoDB)
* Cada mídia pode receber uma nota (1-5) e um comentário do usuário.
* Os dados são armazenados como documentos BSON no MongoDB.

#### 4.1.4 Integração com API Externa
O sistema integra-se a APIs públicas para enriquecer o registo de mídias:
* **Filmes e Séries (TMDb):** Consulta a API do TMDb, com filtro opcional por diretor.
* **Livros (Google Books e Open Library):** Utiliza a API do Google Books (com busca por título/autor) e Open Library como fallback.
* **Músicas (Deezer e Lyrics.ovh):** Consulta a API do Deezer (faixa/artista) e busca a letra na API Lyrics.ovh.

### 4.2 Arquitetura Tecnológica

#### 4.2.1 Frontend
* HTML5, CSS3 e JavaScript (Vanilla).
* Consumo da API REST do backend para todas as operações.

#### 4.2.2 Backend
* Java 21 e Spring Boot.
* Spring Data JPA (MySQL).
* Spring Data MongoDB (MongoDB).
* Spring Security (Autenticação e Autorização).

#### 4.2.3 Bancos de Dados
* **MySQL (Relacional):** Utilizado para dados estruturados (usuários, grupos, mídias e relações). Atende exigências de integridade, transações e uso de triggers, views, e procedures.
* **MongoDB (NoSQL):** Utilizado exclusivamente para a entidade `Avaliacao`, que demanda flexibilidade de esquema e alto volume de escrita.