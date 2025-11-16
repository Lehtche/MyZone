# 6. Controle de Acesso

[Anterior: Banco SQL](./05-Banco-Relacional-MySQL.md) | [Próximo: Banco NoSQL](./07-Banco-NoSQL-MongoDB.md)

O controle de acesso do MyZone foi implementado em duas camadas complementares: no SGBD (MySQL) e no backend (Spring Security), garantindo segurança e organização.

### 6.1 Controle de Acesso no SGBD (MySQL)

Atendendo à exigência de não utilizar o usuário `root`, foi criado um usuário exclusivo para a aplicação:

* **Usuário do banco:** `myzone_app_user`
* **Privilégios:** `SELECT`, `INSERT`, `UPDATE`, `DELETE`, `EXECUTE` (Este último é necessário para chamar a `SP_DeletarMidia`).
* **Escopo:** Restrito apenas ao schema `myzone_db`.

Essa configuração garante isolamento e segurança, já que o usuário da aplicação não possui privilégios administrativos (como `DROP` ou `ALTER`).

### 6.2 Tabelas de Controle de Acesso

Para atender ao requisito obrigatório, foi implementada uma estrutura de autenticação baseada em papéis (roles) usando as tabelas:

1.  **`usuarios`:** Tabela principal contendo email e senha criptografada.
2.  **`grupos_usuarios`:** Armazena os perfis do sistema (ex: `ROLE_USER`, `ROLE_ADMIN`).
3.  **`usuario_grupo`:** Tabela associativa (M:N) que vincula cada usuário a um ou mais grupos.

### 6.3 Controle de Acesso no Backend (Spring Security)

O backend implementa autenticação e autorização utilizando o Spring Security:

* **Login:** Quando o usuário informa email e senha, a aplicação:
    1.  Valida o email no MySQL.
    2.  Compara a senha informada com a senha hasheada (BCrypt) armazenada.
    3.  Carrega os grupos (ex: `ROLE_USER`) vinculados na tabela `usuario_grupo`.
* **Autorização:** Após autenticado, o Spring Security protege os endpoints da API:
    * `/api/usuarios/login` → Acesso público
    * `/api/usuarios/cadastrar` → Acesso público
    * `/api/midias/**` → Exige `ROLE_USER`
    * `/api/avaliacoes/**` → Exige `ROLE_USER`
* **Senhas:** Nenhuma senha é armazenada em texto plano. O backend utiliza hashing seguro (BCrypt) via `PasswordEncoder`, e as senhas criptografadas são persistidas na tabela `usuarios`.