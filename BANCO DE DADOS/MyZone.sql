-- =====================================================================
-- SCRIPT DE CRIAÇÃO COMPLETA DO BANCO DE DADOS "MYZONE"
-- Este script recria todo o esquema gerado pelo Spring Boot
-- e implementa a lógica de SGBD (Grupos, Índices, Views,
-- Triggers e Stored Procedures) exigida pelos requisitos.
-- =====================================================================

-- 1. CRIAÇÃO DA BASE DE DADOS
CREATE DATABASE IF NOT EXISTS myzone_db;
USE myzone_db;

-- =====================================================================
-- 2. CRIAÇÃO DAS TABELAS (ORDEM DE DEPENDÊNCIA)
-- =====================================================================

-- Tabela de Utilizadores (independente)
-- Gerada a partir de Usuario.java
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    data_nascimento DATE,
    email VARCHAR(255),
    nome VARCHAR(255),
    senha VARCHAR(255),
    PRIMARY KEY (id)
);

-- Tabela de Grupos de Utilizadores (independente)
-- Gerada a partir de GrupoUsuario.java (e requisito)
CREATE TABLE IF NOT EXISTS grupos_usuarios (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (nome)
);

-- Tabela de Ligação (Utilizador <-> Grupos)
-- Gerada a partir da anotação @ManyToMany em Usuario.java
CREATE TABLE IF NOT EXISTS usuario_grupo (
    usuario_id BIGINT NOT NULL,
    grupo_id INT NOT NULL,
    PRIMARY KEY (usuario_id, grupo_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (grupo_id) REFERENCES grupos_usuarios(id) ON DELETE CASCADE
);

-- Tabela "Pai" de Mídias (depende de 'usuario')
-- Gerada a partir de Midia.java (Estratégia JOINED)
CREATE TABLE IF NOT EXISTS midia (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255),
    poster_url VARCHAR(255),
    sinopse VARCHAR(2000),
    usuario_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabela "Filha" Filme (depende de 'midia')
-- Gerada a partir de Filme.java
CREATE TABLE IF NOT EXISTS filme (
    ano_lancamento INT NOT NULL,
    diretor VARCHAR(255),
    id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES midia(id) ON DELETE CASCADE
);

-- Tabela "Filha" Livro (depende de 'midia')
-- Gerada a partir de Livro.java
CREATE TABLE IF NOT EXISTS livro (
    autor VARCHAR(255),
    genero VARCHAR(255),
    id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES midia(id) ON DELETE CASCADE
);

-- Tabela "Filha" Musica (depende de 'midia')
-- Gerada a partir de Musica.java
CREATE TABLE IF NOT EXISTS musica (
    album VARCHAR(255),
    artista VARCHAR(255),
    data_estreia DATE,
    id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES midia(id) ON DELETE CASCADE
);

-- Tabela "Filha" Serie (depende de 'midia')
-- Gerada a partir de Serie.java
CREATE TABLE IF NOT EXISTS serie (
    genero VARCHAR(255),
    id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES midia(id) ON DELETE CASCADE
);

-- Tabela Episodio (depende de 'midia' e 'serie')
-- Gerada a partir de Episodio.java
CREATE TABLE IF NOT EXISTS episodio (
    episodio INT NOT NULL,
    temporada INT NOT NULL,
    id BIGINT NOT NULL,
    serie_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES midia(id) ON DELETE CASCADE,
    FOREIGN KEY (serie_id) REFERENCES serie(id) ON DELETE CASCADE
);

-- Tabela Colecao (depende de 'usuario' e 'midia')
-- Gerada a partir de Colecao.java
CREATE TABLE IF NOT EXISTS colecao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255),
    midia_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (midia_id) REFERENCES midia(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- =====================================================================
-- 3. DADOS INICIAIS (Grupos Obrigatórios)
-- =====================================================================
INSERT IGNORE INTO grupos_usuarios (nome) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

-- =====================================================================
-- 4. ÍNDICES (Requisito)
-- =====================================================================
-- JUSTIFICATIVA 1: Otimiza o login (findByEmail)
CREATE UNIQUE INDEX IDX_USUARIO_EMAIL ON usuario(email);

-- JUSTIFICATIVA 2: Otimiza o carregamento do feed (findByCadastradoPor)
CREATE INDEX IDX_MIDIA_USUARIO_ID ON midia(usuario_id);

-- =====================================================================
-- 5. TRIGGERS (Requisito)
-- =====================================================================

-- Tabela de Log para o Trigger 1
CREATE TABLE IF NOT EXISTS log_usuarios_apagados (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id_deletado BIGINT,
    email VARCHAR(255),
    data_delecao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Mudar o delimitador padrão para criar o trigger
DELIMITER $$

-- TRIGGER 1: Auditoria de Exclusão de Utilizador
-- JUSTIFICATIVA: Regista numa tabela de log qual utilizador
-- foi apagado e quando, para fins de auditoria.
CREATE TRIGGER TRG_Audit_Delete_Usuario
BEFORE DELETE ON usuario
FOR EACH ROW
BEGIN
    INSERT INTO log_usuarios_apagados (usuario_id_deletado, email)
    VALUES (OLD.id, OLD.email);
END$$

-- TRIGGER 2: Consistência de Dados (Uppercase)
-- JUSTIFICATIVA: Garante que o nome da mídia seja sempre
-- armazenado em maiúsculas (UPPERCASE) para manter a
-- consistência nos dados e facilitar buscas futuras.
CREATE TRIGGER TRG_Uppercase_Midia_Nome
BEFORE INSERT ON midia
FOR EACH ROW
BEGIN
    SET NEW.nome = UPPER(NEW.nome);
END$$

-- Resetar o delimitador
DELIMITER ;

-- =====================================================================
-- 6. VIEWS (Requisito)
-- =====================================================================

-- VIEW 1: Vista Pública de Utilizadores
-- JUSTIFICATIVA: Cria uma vista segura da tabela 'usuario'
-- que omite dados sensíveis (email, senha).
-- Usada pela API no endpoint /api/usuarios/publicos.
CREATE OR REPLACE VIEW VW_Usuarios_Publicos AS
SELECT
    id,
    nome,
    data_nascimento -- (nome da coluna SQL)
FROM usuario;

-- VIEW 2: Vista Unificada de Mídias
-- JUSTIFICATIVA: Simplifica o acesso aos dados da herança JOINED,
-- unindo 'midia' com todas as suas tabelas-filhas.
CREATE OR REPLACE VIEW VW_Midias_Completas AS
SELECT
    m.id,
    m.nome,
    m.sinopse,
    m.poster_url,      -- (nome da coluna SQL)
    u.nome AS cadastrado_por,
    CASE
        WHEN f.id IS NOT NULL THEN 'FILME'
        WHEN s.id IS NOT NULL THEN 'SERIE'
        WHEN l.id IS NOT NULL THEN 'LIVRO'
        WHEN mu.id IS NOT NULL THEN 'MUSICA'
        ELSE 'MIDIA'
    END AS tipo,
    f.diretor,
    f.ano_lancamento,  -- (nome da coluna SQL)
    s.genero AS genero_serie,
    l.autor,
    l.genero AS genero_livro,
    mu.artista,
    mu.album,
    mu.data_estreia     -- (nome da coluna SQL)
FROM midia m
JOIN usuario u ON m.usuario_id = u.id
LEFT JOIN filme f ON m.id = f.id
LEFT JOIN serie s ON m.id = s.id
LEFT JOIN livro l ON m.id = l.id
LEFT JOIN musica mu ON m.id = mu.id;

-- =====================================================================
-- 7. PROCEDURES E FUNCTIONS (Requisito)
-- =====================================================================

-- Mudar o delimitador
DELIMITER $$

-- FUNCTION 1: Contagem de Mídias por Utilizador
-- JUSTIFICATIVA: Encapsula a lógica de contagem de mídias
-- para um utilizador específico.
CREATE FUNCTION FN_ContarMidiasUsuario(p_usuario_id BIGINT)
RETURNS INT
DETERMINISTIC READS SQL DATA
BEGIN
    DECLARE midia_count INT;
    SELECT COUNT(*) INTO midia_count
    FROM midia
    WHERE usuario_id = p_usuario_id;
    RETURN midia_count;
END$$


-- PROCEDURE 1: Apagar Mídia de forma Segura
-- JUSTIFICATIVA: Encapsula toda a lógica de exclusão de uma mídia,
-- resolvendo o erro de Foreign Key (Error 1451) que ocorre
-- ao tentar apagar 'midia' antes das suas dependências.
-- É chamada pelo MidiaService.
CREATE PROCEDURE SP_DeletarMidia(IN p_midia_id BIGINT, IN p_usuario_id BIGINT)
BEGIN
    DECLARE midia_owner BIGINT;
    
    -- 1. Verifica se o utilizador que está a tentar apagar é o dono
    SELECT usuario_id INTO midia_owner
    FROM midia
    WHERE id = p_midia_id;
    
    IF midia_owner = p_usuario_id THEN
        -- Se for o dono, inicia a transação
        START TRANSACTION;
        
        -- 2. Apaga dependências "externas" (filhos de 1º nível)
        DELETE FROM colecao WHERE midia_id = p_midia_id;
        DELETE FROM episodio WHERE serie_id = p_midia_id;
        
        -- 3. Apaga das tabelas "filhas" (herança JOINED)
        DELETE FROM filme WHERE id = p_midia_id;
        DELETE FROM serie WHERE id = p_midia_id;
        DELETE FROM musica WHERE id = p_midia_id;
        DELETE FROM livro WHERE id = p_midia_id;
        
        -- 4. Finalmente, apaga da tabela "pai"
        DELETE FROM midia WHERE id = p_midia_id;
        
        COMMIT;
    ELSE
        -- Se não for o dono, dispara um erro customizado
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Acesso negado: Você não é o dono desta mídia.';
    END IF;
END$$

-- Resetar o delimitador
DELIMITER ;

-- =====================================================================
-- 8. GESTÃO DE UTILIZADORES DA APLICAÇÃO (Requisito)
-- =====================================================================

-- JUSTIFICATIVA: Este comando cria o utilizador não-root
-- 'myzone_app_user' que a aplicação Spring Boot
-- deve usar. Deve ser executado manualmente uma vez por um
-- utilizador com privilégios (como 'root').

/* -- DESCOMENTE PARA EXECUTAR UMA VEZ
CREATE USER 'myzone_app_user'@'localhost' IDENTIFIED BY 'QWERqwer132';
GRANT SELECT, INSERT, UPDATE, DELETE ON myzone_db.* TO 'myzone_app_user'@'localhost';
GRANT EXECUTE ON PROCEDURE myzone_db.SP_DeletarMidia TO 'myzone_app_user'@'localhost';
GRANT EXECUTE ON FUNCTION myzone_db.FN_ContarMidiasUsuario TO 'myzone_app_user'@'localhost';
FLUSH PRIVILEGES;
*/

-- =====================================================================
-- FIM DO SCRIPT
-- =====================================================================