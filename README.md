# MyZone - Plataforma de Gestão de Mídia

Projeto final para a disciplina de Laboratório de Banco de Dados, focado na implementação de uma aplicação *full-stack* com uma arquitetura de persistência híbrida (SQL + NoSQL).

-----

## 📖 Documentação Técnica Completa

Toda a documentação técnica do projeto, incluindo arquitetura, modelagem de dados, justificativas de SGBD (SQL e NoSQL) e metodologia, está disponível na pasta `/docs`.

**[Clique aqui para ver a documentação completa](./docs/01-Introducao.md)**

### Tópicos da Documentação

* [1. Introdução](./docs/01-Introducao.md)
* [2. Objetivos](./docs/02-Objetivos.md)
* [3. Metodologia](./docs/03-Metodologia.md)
* [4. Descrição do Sistema](./docs/04-Descricao-do-Sistema.md) (Funcionalidades e Tecnologias)
* [5. Modelagem do Banco SQL (MySQL)](./docs/05-Banco-Relacional-MySQL.md)
* [6. Controle de Acesso](./docs/06-Controle-de-Acesso.md)
* [7. Modelagem do Banco NoSQL (MongoDB)](./docs/07-Banco-NoSQL-MongoDB.md)
* [8. Guia de Execução](./docs/08-Guia-de-Execucao.md)
* [9. Conclusão e Referências](./docs/09-Conclusao-e-Referencias.md)

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
* **Integração com API Externa:** Preenchimento automático de dados (ano, realizador, género) de filmes, séries, livros e músicas.