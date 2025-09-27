-- Usar o banco de dados MyZone (certifique-se de que ele já foi criado)
USE MyZone;

-- Inserindo Usuários e Perfis
INSERT INTO Usuarios (idUsuario, nome, email, senha, dataNascimento, dataCadastro) VALUES
(1, 'Alice Rodrigues', 'alice.r@email.com', 'senha123', '1995-05-20', '2023-01-15 10:30:00'),
(2, 'Bruno Carvalho', 'bruno.c@email.com', 'senha456', '1992-09-10', '2023-02-20 11:00:00'),
(3, 'Clara Martins', 'clara.m@email.com', 'senha789', '1998-12-30', '2023-03-25 14:15:00'),
(4, 'Daniel Ferreira', 'daniel.f@email.com', 'senha012', '2000-07-18', '2023-04-10 18:00:00');

INSERT INTO Perfis (idUsuario, biografia, fotoPerfil) VALUES
(1, 'Fã de cinema de arte e literatura clássica.', 'url/fotos/alice.jpg'),
(2, 'Desenvolvedor e entusiasta de séries de ficção científica.', 'url/fotos/bruno.jpg'),
(3, 'Musicista e apreciadora de álbuns conceituais.', 'url/fotos/clara.jpg'),
(4, 'Leitor ávido de fantasia e quadrinhos.', 'url/fotos/daniel.jpg');

-- Inserindo Mídias (Filmes, Séries, Livros, Músicas)
INSERT INTO Midias (idMidia, titulo, capa, anoLancamento) VALUES
(1, 'O Poderoso Chefão', 'url/capas/godfather.jpg', 1972),
(2, 'Blade Runner 2049', 'url/capas/bladerunner.jpg', 2017),
(3, 'Game of Thrones', 'url/capas/got.jpg', 2011),
(4, '1984', 'url/capas/1984.jpg', 1949),
(5, 'The Dark Side of the Moon', 'url/capas/darksidemoon.jpg', 1973),
(6, 'Duna', 'url/capas/dune_book.jpg', 1965);

-- Inserindo nas tabelas especializadas
INSERT INTO Filmes (idMidia, diretor) VALUES (1, 'Francis Ford Coppola'), (2, 'Denis Villeneuve');
INSERT INTO Series (idMidia, diretor, status) VALUES (3, 'David Benioff', 'Finalizada');
INSERT INTO Livros (idMidia, editora, numeroPaginas) VALUES (4, 'Companhia das Letras', 328), (6, 'Aleph', 680);
INSERT INTO Musicas (idMidia, artista, album) VALUES (5, 'Pink Floyd', 'The Dark Side of the Moon');

-- Inserindo Episódios para a série 'Game of Thrones' (idMidia = 3)
INSERT INTO Episodios (idMidia, temporada, numEpisodio, tituloEpisodio) VALUES
(3, 1, 1, 'Winter Is Coming'),
(3, 1, 2, 'The Kingsroad');

-- Inserindo Avaliações
INSERT INTO Avaliacoes (idUsuario, idMidia, nota, comentario, dataAvaliacao) VALUES
(1, 1, 5, 'Um clássico absoluto. Atuações impecáveis.', '2024-01-20'),
(2, 2, 5, 'Visualmente deslumbrante e com uma trama envolvente.', '2024-02-15'),
(1, 4, 5, 'Um livro que continua assustadoramente relevante.', '2024-03-10'),
(3, 5, 5, 'Uma obra-prima sonora que transcende gerações.', '2024-04-05'),
(4, 6, 4, 'Uma leitura densa, mas extremamente recompensadora.', '2024-05-01');

-- Inserindo Amizades
-- Bruno e Alice são amigos (status aceito)
INSERT INTO Amizades (idUsuario1, idUsuario2, status) VALUES (1, 2, 'aceito');
-- Clara enviou um pedido para Alice (status pendente)
INSERT INTO Amizades (idUsuario1, idUsuario2, status) VALUES (3, 1, 'pendente');
-- Daniel e Bruno também são amigos (status aceito)
INSERT INTO Amizades (idUsuario1, idUsuario2, status) VALUES (4, 2, 'aceito');

-- Inserindo Coleções e adicionando mídias a elas
INSERT INTO Colecoes (idUsuario, nomeColecao, privacidade) VALUES
(1, 'Clássicos do Cinema', 'publico'),
(4, 'Épicos da Ficção Científica', 'privado');

INSERT INTO Colecao_Midia (idColecao, idMidia) VALUES
(1, 1), -- Adiciona 'O Poderoso Chefão' à coleção da Alice
(2, 2), -- Adiciona 'Blade Runner 2049' à coleção do Daniel
(2, 6); -- Adiciona 'Duna' à coleção do Daniel

-- Inserindo Notificações
INSERT INTO Notificacoes (idDestinatario, idOriginador, tipo) VALUES
(1, 3, 'pedido_amizade'); -- Notificação para Alice sobre o pedido de amizade de Clara