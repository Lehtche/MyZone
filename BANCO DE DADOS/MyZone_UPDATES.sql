USE MyZone;

-- =====================================================================
-- EXEMPLO 1: ATUALIZAR A BIOGRAFIA DE UM USUÁRIO
-- Cenário: A usuária 'Alice Rodrigues' (idUsuario = 1) quer atualizar sua biografia.
-- =====================================================================

-- Verificação (ANTES): Rode este SELECT para ver a biografia atual.
SELECT * FROM Perfis WHERE idUsuario = 1;

-- O Comando UPDATE:
UPDATE Perfis
SET biografia = 'Apaixonada por cinema de arte, literatura clássica e ficção científica.'
WHERE idUsuario = 1;

-- Verificação (DEPOIS): Rode o mesmo SELECT para confirmar a alteração.
SELECT * FROM Perfis WHERE idUsuario = 1;


-- =====================================================================
-- EXEMPLO 2: ACEITAR UM PEDIDO DE AMIZADE
-- Cenário: 'Alice Rodrigues' (idUsuario = 1) aceita o pedido de 'Clara Martins' (idUsuario = 3).
-- O status do pedido (3 -> 1) muda de 'pendente' para 'aceito'.
-- =====================================================================

-- Verificação (ANTES): Veja o status atual do pedido de amizade.
SELECT * FROM Amizades WHERE idUsuario1 = 3 AND idUsuario2 = 1;

-- O Comando UPDATE:
UPDATE Amizades
SET status = 'aceito'
WHERE idUsuario1 = 3 AND idUsuario2 = 1;

-- Lógica Adicional (RECOMENDADO): Inserir a relação inversa para facilitar consultas.
INSERT INTO Amizades (idUsuario1, idUsuario2, status)
VALUES (1, 3, 'aceito')
-- A linha abaixo previne erro caso a relação inversa já exista.
ON DUPLICATE KEY UPDATE status = 'aceito';

-- Verificação (DEPOIS): A consulta agora deve mostrar a amizade como 'aceita' e a relação inversa criada.
SELECT * FROM Amizades WHERE (idUsuario1 = 3 AND idUsuario2 = 1) OR (idUsuario1 = 1 AND idUsuario2 = 3);


-- =====================================================================
-- EXEMPLO 3: MODIFICAR UMA AVALIAÇÃO EXISTENTE
-- Cenário: 'Daniel Ferreira' (idUsuario = 4) releu o livro 'Duna' (idMidia = 6) e
-- decidiu mudar sua nota de 4 para 5 e refinar seu comentário.
-- =====================================================================

-- Verificação (ANTES): Veja a avaliação original.
SELECT * FROM Avaliacoes WHERE idUsuario = 4 AND idMidia = 6;

-- O Comando UPDATE:
UPDATE Avaliacoes
SET
    nota = 5,
    comentario = 'Após uma releitura atenta, confirmo que é uma obra-prima. Essencial para fãs de sci-fi.'
WHERE
    idUsuario = 4 AND idMidia = 6;

-- Verificação (DEPOIS): Confirme a nova nota e comentário.
SELECT * FROM Avaliacoes WHERE idUsuario = 4 AND idMidia = 6;


-- =====================================================================
-- EXEMPLO 4: ALTERAR O STATUS DE UMA SÉRIE
-- Cenário: A série 'Game of Thrones' (idMidia = 3) anuncia um spin-off,
-- e a plataforma decide alterar o status de 'Finalizada' para 'Em andamento'
-- para refletir o novo conteúdo no universo da franquia.
-- =====================================================================

-- Verificação (ANTES): Veja o status atual da série.
SELECT m.titulo, s.status
FROM Series s
JOIN Midias m ON s.idMidia = m.idMidia
WHERE s.idMidia = 3;

-- O Comando UPDATE:
UPDATE Series
SET status = 'Em andamento'
WHERE idMidia = 3;

-- Verificação (DEPOIS): Confirme o novo status.
SELECT m.titulo, s.status
FROM Series s
JOIN Midias m ON s.idMidia = m.idMidia
WHERE s.idMidia = 3;