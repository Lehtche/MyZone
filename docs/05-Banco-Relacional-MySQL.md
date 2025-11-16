# 5. Modelagem do Banco de Dados Relacional (MySQL)

[Anterior: Descrição do Sistema](./04-Descricao-do-Sistema.md) | [Próximo: Controle de Acesso](./06-Controle-de-Acesso.md)

O MySQL foi utilizado como SGBD relacional para armazenar os dados estruturados e críticos do sistema (usuários, mídias, etc.). Todo o modelo foi criado garantindo integridade referencial, normalização e utilização de recursos avançados do SGBD.

### 5.1 Diagrama Entidade-Relacionamento (DER)

A Figura 1 apresenta o Diagrama Entidade-Relacionamento do banco de dados MySQL utilizado pelo sistema.

*Figura 1 – Diagrama Entidade-Relacionamento do MyZone*
![DER do MyZone](httpsimages/der.png)
[cite_start]*(Nota: Você precisará adicionar a imagem do DER do seu .docx [cite: 191] a uma pasta `docs/images` e referenciá-la aqui)*

O modelo adota:
* Herança do tipo `JOINED` para as categorias de mídia.
* Relacionamentos 1:N, 1:1, e M:N.
* Tabelas auxiliares obrigatórias (`usuarios`, `grupos_usuarios`, `usuario_grupo`).

### 5.2 Descrição das Entidades

* **usuario:** Armazena informações básicas dos usuários (nome, e-mail, senha).
* **grupos_usuarios:** Define os grupos/permissões (ex.: ROLE_USER).
* **usuario_grupo:** Entidade associativa (M:N) entre usuários e grupos.
* **Midia:** Tabela "superclasse" que armazena atributos comuns (nome, poster_url, sinopse, usuario_id). O campo `sinopse` é `TEXT` para suportar dados longos (como letras de música).
* **filme, serie, musica, livro:** Tabelas "filhas" (Herança JOINED) que armazenam atributos específicos (ex: `filme` tem `diretor`, `livro` tem `autor`).
* **episodio:** Relacionada à `serie` (1:N).
* **colecao:** Implementa um relacionamento M:N entre usuários e mídias.
* **log_usuarios_apagados:** Tabela de auditoria utilizada pelo trigger de exclusão.

### 5.3 Justificativas Técnicas (Recursos Avançados)

#### 5.3.1 Índices

1.  **`IDX_USUARIO_EMAIL` (UNIQUE) – Tabela `usuario`**
    * **Justificativa:** Garante unicidade do e-mail no cadastro e otimiza drasticamente a performance da consulta no login (`findByEmail`), uma operação extremamente frequente.
2.  **`IDX_MIDIA_USUARIO_ID` – Tabela `midia`**
    * **Justificativa:** Indexa a chave estrangeira `usuario_id`. Melhora significativamente a performance do carregamento do "feed" principal do usuário (`findByCadastradoPor`).

#### 5.3.2 Triggers

1.  **`TRG_Audit_Delete_Usuario` (BEFORE DELETE)**
    * **Justificativa:** Garante rastreabilidade e auditoria. Antes de um usuário ser deletado, este trigger copia informações essenciais (ID, email) para a tabela `log_usuarios_apagados`.
2.  **`TRG_Uppercase_Midia_Nome` (BEFORE INSERT)**
    * **Justificativa:** Mantém a consistência dos dados. Converte o nome de todas as mídias para `UPPERCASE` antes da inserção, evitando duplicação por variação de caixa (ex: "Matrix" vs "matrix").

#### 5.3.3 Views

1.  **`vw_usuarios_publicos`**
    * **Justificativa:** Essencial para segurança. A view expõe dados não sensíveis dos usuários (ID, nome, data_nascimento), ocultando e-mail e senha. É usada pela API para listar usuários publicamente.
2.  **`vw_midias_completas`**
    * **Justificativa:** Simplifica consultas complexas. Esta view "junta" a tabela `midia` com todas as suas tabelas-filhas (filme, serie, etc.) usando `LEFT JOIN`, facilitando a leitura consolidada de dados para relatórios.

#### 5.3.4 Procedures e Functions

1.  **`FN_ContarMidiasUsuario` (Function)**
    * **Justificativa:** Encapsula lógica de uso recorrente. Retorna a quantidade total de mídias cadastradas por um ID de usuário específico.
2.  **`SP_DeletarMidia` (Procedure)**
    * **Justificativa:** Resolve problemas de integridade referencial (erro 1451). A exclusão de uma `midia` é complexa, pois requer a exclusão de dependências em tabelas-filhas (`filme`, `serie`, etc.) e externas (`colecao`, `episodio`). Esta procedure executa a exclusão na ordem correta, garantindo uma operação transacional e segura, sendo chamada diretamente pelo `MidiaRepository` no backend.

### 5.4 Geração de IDs

A aplicação utiliza `AUTO_INCREMENT` (mapeado como `GenerationType.IDENTITY` no Java), conforme permitido, e a escolha é justificada por:
* Melhor desempenho em índices clusterizados (InnoDB).
* Simplicidade na manutenção da integridade referencial.
* Ausência de necessidade de IDs criptográficos ou regras de negócio complexas para a geração de IDs neste contexto.