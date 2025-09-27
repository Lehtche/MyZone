-- =====================================================================
-- SCRIPT DE CRIAÇÃO DO BANCO DE DADOS - APLICATIVO DE AVALIAÇÃO DE MÍDIA
-- =====================================================================

-- Garante que o banco de dados seja recriado do zero a cada execução.
DROP DATABASE IF EXISTS MyZone;
CREATE DATABASE MyZone;
USE MyZone;

-- =====================================================================
-- TABELAS PRINCIPAIS (ENTIDADES FORTES)
-- =====================================================================

CREATE TABLE Usuarios (
    idUsuario INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE, -- UNIQUE garante que não haja e-mails repetidos
    senha VARCHAR(255) NOT NULL,
    dataNascimento DATE,
    dataCadastro DATETIME NOT NULL
);

CREATE TABLE Perfis (
    idUsuario INT PRIMARY KEY, -- É PK e FK ao mesmo tempo, garantindo a relação 1:1
    biografia TEXT,
    fotoPerfil VARCHAR(255), -- Armazena a URL ou caminho do arquivo da foto
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario)
);

CREATE TABLE Midias (
    idMidia INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    capa VARCHAR(255), -- Armazena a URL ou caminho do arquivo da capa
    anoLancamento INT
);

CREATE TABLE Colecoes (
    idColecao INT PRIMARY KEY AUTO_INCREMENT,
    idUsuario INT NOT NULL, -- FK para saber a quem pertence a coleção
    nomeColecao VARCHAR(255) NOT NULL,
    capaColecao VARCHAR(255),
    privacidade ENUM('publico', 'privado', 'so_amigos') NOT NULL DEFAULT 'privado',
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario)
);

-- =====================================================================
-- TABELAS DE ESPECIALIZAÇÃO (HERANÇA DE MIDIA)
-- =====================================================================

CREATE TABLE Filmes (
    idMidia INT PRIMARY KEY, -- É PK e FK, herdando a identidade de Midia
    diretor VARCHAR(255),
    FOREIGN KEY (idMidia) REFERENCES Midias(idMidia)
);

CREATE TABLE Musicas (
    idMidia INT PRIMARY KEY,
    artista VARCHAR(255),
    album VARCHAR(255),
    FOREIGN KEY (idMidia) REFERENCES Midias(idMidia)
);

CREATE TABLE Livros (
    idMidia INT PRIMARY KEY,
    editora VARCHAR(255),
    numeroPaginas INT,
    FOREIGN KEY (idMidia) REFERENCES Midias(idMidia)
);

CREATE TABLE Series (
    idMidia INT PRIMARY KEY,
    diretor VARCHAR(255),
    status ENUM('Em andamento', 'Finalizada', 'Cancelada') NOT NULL,
    FOREIGN KEY (idMidia) REFERENCES Midias(idMidia)
);

-- =====================================================================
-- ENTIDADE FRACA
-- =====================================================================

CREATE TABLE Episodios (
    idMidia INT, -- Parte da PK, FK para a tabela Serie
    temporada INT, -- Parte da PK
    numEpisodio INT, -- Parte da PK
    tituloEpisodio VARCHAR(255),
    PRIMARY KEY (idMidia, temporada, numEpisodio), -- Chave primária composta
    FOREIGN KEY (idMidia) REFERENCES Series(idMidia)
);

-- =====================================================================
-- TABELAS ASSOCIATIVAS (RELACIONAMENTOS N:M)
-- =====================================================================

CREATE TABLE Avaliacoes (
    idAvaliacao INT PRIMARY KEY AUTO_INCREMENT, -- Chave substituta para facilitar referências
    idUsuario INT NOT NULL,
    idMidia INT NOT NULL,
    nota INT,
    comentario TEXT,
    dataAvaliacao DATE,
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario),
    FOREIGN KEY (idMidia) REFERENCES Midias(idMidia),
    UNIQUE (idUsuario, idMidia) -- Garante que um usuário só pode avaliar uma mídia uma vez
);

CREATE TABLE Amizades (
    idAmizade INT PRIMARY KEY AUTO_INCREMENT,
    idUsuario1 INT NOT NULL, -- Quem enviou o pedido
    idUsuario2 INT NOT NULL, -- Quem recebeu o pedido
    status ENUM('pendente', 'aceito', 'recusado', 'bloqueado') NOT NULL DEFAULT 'pendente',
    FOREIGN KEY (idUsuario1) REFERENCES Usuarios(idUsuario),
    FOREIGN KEY (idUsuario2) REFERENCES Usuarios(idUsuario),
    UNIQUE (idUsuario1, idUsuario2) -- Impede que o mesmo pedido de amizade seja feito duas vezes
);

CREATE TABLE Colecao_Midia (
    idColecao INT,
    idMidia INT,
    PRIMARY KEY (idColecao, idMidia), -- Chave composta
    FOREIGN KEY (idColecao) REFERENCES Colecoes(idColecao),
    FOREIGN KEY (idMidia) REFERENCES Midias(idMidia)
);

CREATE TABLE Usuario_Cadastra_Midia (
    idUsuario INT,
    idMidia INT,
    dataCadastro DATETIME NOT NULL,
    PRIMARY KEY (idUsuario, idMidia),
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario),
    FOREIGN KEY (idMidia) REFERENCES Midias(idMidia)
);

-- =====================================================================
-- TABELA PARA FUNCIONALIDADES DO APLICATIVO
-- =====================================================================

CREATE TABLE Notificacoes (
    idNotificacao INT PRIMARY KEY AUTO_INCREMENT,
    idDestinatario INT NOT NULL, -- Quem recebe a notificação
    idOriginador INT NOT NULL, -- Quem causou a ação
    tipo VARCHAR(50) NOT NULL, -- Ex: 'nova_avaliacao', 'pedido_amizade'
    idConteudo INT, -- ID da avaliação, amizade, etc.
    lida BOOLEAN NOT NULL DEFAULT FALSE,
    dataCriacao DATETIME NOT NULL default(current_timestamp()),
    FOREIGN KEY (idDestinatario) REFERENCES Usuarios(idUsuario),
    FOREIGN KEY (idOriginador) REFERENCES Usuarios(idUsuario)
);

-- =====================================================================
