-----

# MyZone - Plataforma de Gestão de Mídia

Projeto final para a disciplina de Laboratório de Banco de Dados, focado na implementação de uma aplicação *full-stack* com uma arquitetura de persistência híbrida (SQL + NoSQL).

-----

## 📖 Sobre o Projeto

O **MyZone** é uma aplicação web social que permite aos utilizadores catalogar e gerir as mídias que consomem, como filmes, séries, músicas e livros.

O principal objetivo deste projeto não é apenas criar uma aplicação funcional, mas sim demonstrar o domínio de conceitos avançados de SGBD, implementando uma arquitetura de persistência híbrida:

1.  **MySQL (Banco Relacional):** Utilizado para armazenar os dados estruturados e transacionais, como utilizadores, metadados de mídias e os seus relacionamentos (herança, coleções).
2.  **MongoDB (Banco NoSQL):** Utilizado para armazenar dados não estruturados, de escrita intensiva e esquema flexível, como as avaliações (notas e comentários) dos utilizadores.

O sistema é composto por um backend RESTful em **Java/Spring Boot** e um frontend SPA (Single Page Application) em **HTML, CSS e JavaScript**.

-----

## ✨ Funcionalidades

  * **Autenticação de Utilizadores:** Sistema completo de registo (com criptografia de senha) e login.
  * **Controlo de Acesso por Grupos:** Acesso à API protegido por permissões (`ROLE_USER`), implementado com Spring Security e tabelas `grupos_usuarios`.
  * **CRUD Completo de Mídias:**
      * **C**reate: Adicionar novas mídias.
      * **R**ead: Ler o feed de mídias e ver detalhes.
      * **U**pdate: Editar mídias existentes.
      * **D**elete: Apagar mídias (controlado por Stored Procedure).
  * **Arquitetura Híbrida:** Salva metadados de mídias no MySQL e as suas avaliações (notas/comentários) no MongoDB.
  * **Integração com API Externa:** Preenchimento automático de dados (ano, realizador, género) de filmes e séries através da API do TMDb.

-----

## 💻 Tecnologias Utilizadas

### Backend

  * **Java 21**
  * **Spring Boot 3.5.7**
  * **Spring Security:** Para autenticação e controlo de acesso baseado em *roles*.
  * **Spring Data JPA:** Para persistência relacional (MySQL).
  * **Spring Data MongoDB:** Para persistência NoSQL (MongoDB).
  * **Maven:** Gestão de dependências.

### Frontend

  * **HTML5**
  * **CSS3**
  * **JavaScript (Vanilla ES6+)**

### Bancos de Dados

  * **MySQL 8.0:** SGBD Relacional principal.
  * **MongoDB 7.0:** SGBD NoSQL para dados de avaliações.

-----

## 🏛️ Arquitetura e Destaques do SGBD

O núcleo deste projeto é a demonstração de funcionalidades avançadas de SGBD, conforme os requisitos. O script `MyZone.sql` implementa:

  * **Utilizador Dedicado (Não-Root):** A aplicação acede ao MySQL com o utilizador `myzone_app_user`, que tem permissões limitadas, em vez de usar `root`.
  * **Stored Procedure (`SP_DeletarMidia`):** Encapsula a lógica complexa de apagar uma mídia, garantindo que todas as suas dependências (`colecao`, `episodio`, e tabelas-filhas de herança) sejam removidas na ordem correta, evitando erros de *Foreign Key Constraint*. Esta procedure é chamada diretamente pelo `MidiaService`.
  * **Views (2):**
    1.  `VW_Usuarios_Publicos`: Cria uma abstração segura da tabela `usuario`, expondo apenas dados não-sensíveis (sem email/senha). É usada pela API para listagens públicas.
    2.  `VW_Midias_Completas`: Simplifica a complexidade da herança (`JOINED`), unindo a tabela `midia` com todas as suas filhas (`filme`, `serie`, etc.) para facilitar a criação de relatórios.
  * **Triggers (2):**
    1.  `TRG_Audit_Delete_Usuario`: Regista numa tabela de `log_usuarios_apagados` sempre que um utilizador é apagado, para fins de auditoria.
    2.  `TRG_Uppercase_Midia_Nome`: Garante a consistência dos dados ao formatar todos os nomes de mídia para `UPPERCASE` no `INSERT`.
  * **Índices (2):**
    1.  `IDX_USUARIO_EMAIL`: Otimiza a pesquisa de utilizador por email no login.
    2.  `IDX_MIDIA_USUARIO_ID`: Otimiza o carregamento do feed de mídias do utilizador.
  * **Geração de IDs (Justificativa):** O projeto utiliza `AUTO_INCREMENT` (`GenerationType.IDENTITY`). Esta escolha é justificada pela performance de indexação e simplicidade, dado que os IDs da aplicação não são dados de negócio críticos que exijam regras de geração complexas (como UUIDs).

-----

## 🚀 Como Executar o Projeto

### Pré-requisitos

  * JDK 21 ou superior.
  * Maven 3.x.
  * Servidor MySQL (recomendado 8.0) a correr em `localhost:3306`.
  * Servidor MongoDB (recomendado 7.0) a correr em `localhost:27017`.
  * Uma chave de API do [The Movie Database (TMDb)](https://www.themoviedb.org/documentation/api) (é gratuita).

### 1\. Clonar o Repositório

```bash
git clone https://(url-do-teu-repositorio-git)/MyZone.git
cd MyZone/POO/Aplicação
```

### 2\. Configurar a Base de Dados (MySQL)

1.  Abra o teu cliente MySQL (Workbench, DBeaver, etc.) como utilizador `root`.
2.  Execute o script SQL completo localizado em:
    `MyZone/BANCO DE DADOS/MyZone.sql`
3.  Este script fará tudo:
      * Cria a base de dados `myzone_db`.
      * Cria todas as tabelas (`usuario`, `midia`, `filme`, `grupos_usuarios`, etc.).
      * Insere os grupos (`ROLE_USER`, `ROLE_ADMIN`).
      * Cria os Índices, Triggers, Views e a Stored Procedure.
      * **Importante:** Cria o utilizador `myzone_app_user` com uma senha padrão. (Se mudares a senha no script, lembra-te dela).

### 3\. Configurar a Base de Dados (MongoDB)

Não precisas de fazer nada. Apenas garante que o serviço do MongoDB está a correr na porta `27017`. O Spring Boot criará automaticamente a base de dados `myzone_mongo_db` e a coleção `avaliacoes` no primeiro uso.

### 4\. Configurar a Aplicação (Backend)

1.  Abra o ficheiro: `MyZone/POO/Aplicação/src/main/resources/application.yaml`.

2.  Atualize os seguintes campos:

    ```yaml
    spring:
      datasource:
        # Confirma que o username é 'myzone_app_user'
        username: myzone_app_user
        # Atualiza a senha para a que definiste no script MyZone.sql
        password: QWERqwer132 # (ou a senha que escolheste)

    # ...

    tmdb:
      api:
        # Insere a tua chave de API v3 do TMDb
        key: "A_TUA_CHAVE_API_TMDB_AQUI" 
    ```

### 5\. Executar o Backend

Podes executar a aplicação de duas formas:

**Via Maven (Terminal):**

```bash
# Dentro da pasta MyZone/POO/Aplicação/
./mvnw spring-boot:run
```

**Via IDE (Recomendado):**

  * Importa o projeto como um "Existing Maven Project".
  * Executa a classe principal `MyZoneApplication.java`.

### 6\. Aceder ao Frontend

O backend serve o frontend automaticamente. Abre o teu navegador e acede a:

➡️ **http://localhost:8080**

Podes agora registar um novo utilizador, fazer login e testar todas as funcionalidades do CRUD.

-----

## 👨‍💻 Autores

*(Insere o(s) teu(s) nome(s) e informações aqui)*
