-- =====================================================================
-- SCRIPT COMPLETO: 4 SELECTs, 4 UPDATEs e 4 DELETEs
-- Baseado na população de dados e nos exemplos de UPDATE fornecidos.
-- =====================================================================

-- Garante que todos os comandos serão executados no banco de dados correto.
USE MyZone;


-- =====================================================================
-- PARTE 1: 4 EXEMPLOS DE CONSULTA (SELECT)
-- =====================================================================

-- CONSULTA 1: Listar os usuários que mais fizeram avaliações.
-- Demonstra o uso de JOIN, COUNT, GROUP BY e ORDER BY para criar um ranking.
SELECT
    u.nome,
    COUNT(a.idAvaliacao) AS total_de_avaliacoes
FROM
    Usuarios u
JOIN
    Avaliacoes a ON u.idUsuario = a.idUsuario
GROUP BY
    u.nome
ORDER BY
    total_de_avaliacoes DESC;


-- CONSULTA 2: Encontrar todos os filmes dirigidos por 'Denis Villeneuve'.
-- Demonstra o JOIN com uma tabela especializada (Filmes) para filtrar por um atributo específico.
SELECT
    m.titulo,
    m.anoLancamento,
    f.diretor
FROM
    Midias m
JOIN
    Filmes f ON m.idMidia = f.idMidia
WHERE
    f.diretor = 'Denis Villeneuve';


-- CONSULTA 3: Listar todas as mídias na coleção 'Épicos da Ficção Científica' do usuário 'Daniel Ferreira'.
-- Demonstra como navegar em um relacionamento N:M para ver o conteúdo de uma coleção.
SELECT
    c.nomeColecao,
    u.nome AS dono_da_colecao,
    m.titulo AS midia_na_colecao
FROM
    Colecoes c
JOIN
    Usuarios u ON c.idUsuario = u.idUsuario
JOIN
    Colecao_Midia cm ON c.idColecao = cm.idColecao
JOIN
    Midias m ON cm.idMidia = m.idMidia
WHERE
    u.nome = 'Daniel Ferreira' AND c.nomeColecao = 'Épicos da Ficção Científica';


-- CONSULTA 4: Mostrar quais usuários têm pedidos de amizade pendentes para serem aceitos.
-- Demonstra como usar JOINs em uma tabela de autorrelacionamento para extrair informações sociais.
SELECT
    destinatario.nome AS usuario_notificado,
    remetente.nome AS solicitante,
    a.status
FROM
    Amizades a
JOIN
    Usuarios remetente ON a.idUsuario1 = remetente.idUsuario
JOIN
    Usuarios destinatario ON a.idUsuario2 = destinatario.idUsuario
WHERE
    a.status = 'pendente';


-- =====================================================================
-- PARTE 2: 4 EXEMPLOS DE ATUALIZAÇÃO (UPDATE)
-- (Estes são os exemplos que você forneceu, adaptados aqui.)
-- =====================================================================

-- UPDATE 1: ATUALIZAR A BIOGRAFIA DE UM USUÁRIO
-- Cenário: A usuária 'Alice Rodrigues' (idUsuario = 1) quer atualizar sua biografia.
-- Verificação (ANTES):
SELECT * FROM Perfis WHERE idUsuario = 1;

UPDATE Perfis
SET biografia = 'Apaixonada por cinema de arte, literatura clássica e ficção científica.'
WHERE idUsuario = 1;

-- Verificação (DEPOIS):
SELECT * FROM Perfis WHERE idUsuario = 1;


-- UPDATE 2: ACEITAR UM PEDIDO DE AMIZADE
-- Cenário: 'Alice Rodrigues' (idUsuario = 1) aceita o pedido de 'Clara Martins' (idUsuario = 3).
-- Verificação (ANTES):
SELECT * FROM Amizades WHERE idUsuario1 = 3 AND idUsuario2 = 1;

UPDATE Amizades
SET status = 'aceito'
WHERE idUsuario1 = 3 AND idUsuario2 = 1;

-- Lógica Adicional (RECOMENDADO): Inserir a relação inversa para facilitar consultas.
INSERT INTO Amizades (idUsuario1, idUsuario2, status)
VALUES (1, 3, 'aceito')
ON DUPLICATE KEY UPDATE status = 'aceito';

-- Verificação (DEPOIS):
SELECT * FROM Amizades WHERE (idUsuario1 = 3 AND idUsuario2 = 1) OR (idUsuario1 = 1 AND idUsuario2 = 3);


-- UPDATE 3: MODIFICAR UMA AVALIAÇÃO EXISTENTE
-- Cenário: 'Daniel Ferreira' (idUsuario = 4) releu o livro 'Duna' (idMidia = 6) e mudou sua nota e comentário.
-- Verificação (ANTES):
SELECT * FROM Avaliacoes WHERE idUsuario = 4 AND idMidia = 6;

UPDATE Avaliacoes
SET
    nota = 5,
    comentario = 'Após uma releitura atenta, confirmo que é uma obra-prima. Essencial para fãs de sci-fi.'
WHERE
    idUsuario = 4 AND idMidia = 6;

-- Verificação (DEPOIS):
SELECT * FROM Avaliacoes WHERE idUsuario = 4 AND idMidia = 6;


-- UPDATE 4: ALTERAR O STATUS DE UMA SÉRIE
-- Cenário: A série 'Game of Thrones' (idMidia = 3) anuncia um spin-off, e o status muda para 'Em andamento'.
-- Verificação (ANTES):
SELECT m.titulo, s.status FROM Series s JOIN Midias m ON s.idMidia = m.idMidia WHERE s.idMidia = 3;

UPDATE Series
SET status = 'Em andamento'
WHERE idMidia = 3;

-- Verificação (DEPOIS):
SELECT m.titulo, s.status FROM Series s JOIN Midias m ON s.idMidia = m.idMidia WHERE s.idMidia = 3;


-- =====================================================================
-- PARTE 3: 4 EXEMPLOS DE EXCLUSÃO (DELETE)
-- ATENÇÃO: Comandos DELETE são permanentes. Use a cláusula WHERE!
-- =====================================================================

-- DELETE 1: Remover uma avaliação específica.
-- Cenário: 'Bruno Carvalho' (idUsuario = 2) decidiu remover sua avaliação de 'Blade Runner 2049' (idMidia = 2).
-- Verificação (ANTES):
SELECT * FROM Avaliacoes WHERE idUsuario = 2 AND idMidia = 2;

DELETE FROM Avaliacoes
WHERE idUsuario = 2 AND idMidia = 2;

-- Verificação (DEPOIS): A consulta não deve retornar nada.
SELECT * FROM Avaliacoes WHERE idUsuario = 2 AND idMidia = 2;


-- DELETE 2: Remover uma mídia de uma coleção.
-- Cenário: 'Daniel Ferreira' (dono da coleção id 2) removeu 'Blade Runner 2049' (idMidia = 2) de sua coleção.
-- Verificação (ANTES):
SELECT * FROM Colecao_Midia WHERE idColecao = 2 AND idMidia = 2;

DELETE FROM Colecao_Midia
WHERE idColecao = 2 AND idMidia = 2;

-- Verificação (DEPOIS): A consulta não deve retornar nada.
SELECT * FROM Colecao_Midia WHERE idColecao = 2 AND idMidia = 2;


-- DELETE 3: Desfazer uma amizade.
-- Cenário: 'Daniel Ferreira' (idUsuario = 4) e 'Bruno Carvalho' (idUsuario = 2) não são mais amigos.
-- IMPORTANTE: É preciso remover as duas "direções" da amizade.
-- Verificação (ANTES):
SELECT * FROM Amizades WHERE (idUsuario1 = 4 AND idUsuario2 = 2) OR (idUsuario1 = 2 AND idUsuario2 = 4);

DELETE FROM Amizades
WHERE (idUsuario1 = 4 AND idUsuario2 = 2) OR (idUsuario1 = 2 AND idUsuario2 = 4);

-- Verificação (DEPOIS): A consulta não deve retornar nada.
SELECT * FROM Amizades WHERE (idUsuario1 = 4 AND idUsuario2 = 2) OR (idUsuario1 = 2 AND idUsuario2 = 4);


-- DELETE 4: Excluir uma notificação antiga.
-- Cenário: A notificação sobre o pedido de amizade de Clara para Alice (idNotificacao = 1) será excluída.
-- Verificação (ANTES):
SELECT * FROM Notificacoes WHERE idNotificacao = 1;

DELETE FROM Notificacoes
WHERE idNotificacao = 1;

-- Verificação (DEPOIS): A consulta não deve retornar nada.
SELECT * FROM Notificacoes WHERE idNotificacao = 1;