use MyZone;

-- 1. Listar todos os livros, seus autores (editoras) e número de páginas.
SELECT
    m.titulo AS Titulo,
    m.anoLancamento AS 'Ano de Lançamento',
    l.editora AS Editora,
    l.numeroPaginas AS 'Nº de Páginas'
FROM
    Midias m
JOIN
    Livros l ON m.idMidia = l.idMidia;


-- 2. Exibir todas as avaliações de um usuário específico (Alice Rodrigues).
SELECT
    u.nome AS Usuario,
    m.titulo AS Midia,
    a.nota AS Nota,
    a.comentario AS Comentario
FROM
    Avaliacoes a
JOIN
    Usuarios u ON a.idUsuario = u.idUsuario
JOIN
    Midias m ON a.idMidia = m.idMidia
WHERE
    u.nome = 'Alice Rodrigues';
    
-- 3. Calcular a nota média de cada mídia que possui avaliação.
SELECT
    m.titulo AS Midia,
    ROUND(AVG(a.nota), 2) AS Nota_Media,
    COUNT(a.idAvaliacao) AS Total_Avaliacoes
FROM
    Midias m
JOIN
    Avaliacoes a ON m.idMidia = a.idMidia
GROUP BY
    m.titulo
ORDER BY
    Nota_Media DESC;
    
-- 4. Listar as mídias na coleção "Épicos da Ficção Científica".
SELECT
    c.nomeColecao AS Colecao,
    u.nome AS Dono,
    m.titulo AS Midia
FROM
    Colecoes c
JOIN
    Usuarios u ON c.idUsuario = u.idUsuario
JOIN
    Colecao_Midia cm ON c.idColecao = cm.idColecao
JOIN
    Midias m ON cm.idMidia = m.idMidia
WHERE
    c.nomeColecao = 'Épicos da Ficção Científica';


-- 5. Exibir os títulos dos episódios da primeira temporada de Game of Thrones.
SELECT
    m.titulo AS Serie,
    e.temporada AS Temporada,
    e.numEpisodio AS Episodio,
    e.tituloEpisodio AS 'Título do Episódio'
FROM
    Episodios e
JOIN
    Midias m ON e.idMidia = m.idMidia
WHERE
    m.titulo = 'Game of Thrones' AND e.temporada = 1;


-- 7. Mostrar todos os pedidos de amizade com status 'aceito'.
SELECT
    remetente.nome AS Remetente,
    destinatario.nome AS Destinatario,
    a.status AS Status
FROM
    Amizades a
JOIN
    Usuarios remetente ON a.idUsuario1 = remetente.idUsuario
JOIN
    Usuarios destinatario ON a.idUsuario2 = destinatario.idUsuario
WHERE
    a.status = 'aceito';

-- 7. Mostrar todas as amizades com status 'aceito' do usuário 'Bruno Carvalho'.
SELECT
    CASE
        WHEN u1.nome = 'Bruno Carvalho' THEN u2.nome
        ELSE u1.nome
    END AS Amigo
FROM
    Amizades a
JOIN
    Usuarios u1 ON a.idUsuario1 = u1.idUsuario
JOIN
    Usuarios u2 ON a.idUsuario2 = u2.idUsuario
WHERE
    (u1.nome = 'Bruno Carvalho' OR u2.nome = 'Bruno Carvalho') AND a.status = 'aceito';