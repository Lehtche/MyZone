drop database if exists MyZone;
create database if not exists MyZone;
use MyZone;


CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE midia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    idUsuario INT NOT NULL, -- cadastradoPor
    FOREIGN KEY (idUsuario) REFERENCES usuario(id)
);

CREATE TABLE filme (
    id INT PRIMARY KEY,
    diretor VARCHAR(100),
    duracao INT,
    FOREIGN KEY (id) REFERENCES midia(id)
);

CREATE TABLE livro (
    id INT PRIMARY KEY,
    autor VARCHAR(100),
    paginas INT,
    FOREIGN KEY (id) REFERENCES midia(id)
);

CREATE TABLE musica (
    id INT PRIMARY KEY,
    artista VARCHAR(100),
    duracao INT,
    FOREIGN KEY (id) REFERENCES midia(id)
);

CREATE TABLE serie (
    id INT PRIMARY KEY,
    temporadas INT,
    FOREIGN KEY (id) REFERENCES midia(id)
);

CREATE TABLE episodio (
    id INT PRIMARY KEY,
    temporada INT,
    episodio INT,
    idSerie INT,
    FOREIGN KEY (id) REFERENCES midia(id),
    FOREIGN KEY (idSerie) REFERENCES serie(id)
);

CREATE TABLE avaliacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT NOT NULL,
    idMidia INT NOT NULL,
    nota INT CHECK (nota BETWEEN 0 AND 10),
    comentario VARCHAR(255),
    dataAvaliacao DATE,
    FOREIGN KEY (idUsuario) REFERENCES usuario(id),
    FOREIGN KEY (idMidia) REFERENCES midia(id)
);
insert into usuario values(1,"João Vitor", "joaovitor@a.ucb.br", "joaovitor");
select * from usuario;
SELECT 
            m.id, m.nome, m.idUsuario, 
            u.id AS u_id, u.nome AS u_nome, u.email AS u_email, u.senha AS u_senha, 
            f.diretor, f.duracao AS f_duracao,
            l.autor, l.paginas, 
            mu.artista, mu.duracao AS mu_duracao, 
            s.temporadas,
            e.temporada, e.episodio, e.idSerie, 
            m_serie.nome AS nomeDaSerie, 
            s_serie.temporadas AS temporadasDaSerie
            FROM midia m 
            JOIN usuario u ON m.idUsuario = u.id 
            LEFT JOIN filme f ON m.id = f.id 
            LEFT JOIN livro l ON m.id = l.id
            LEFT JOIN musica mu ON m.id = mu.id
            LEFT JOIN serie s ON m.id = s.id
            LEFT JOIN episodio e ON m.id = e.id
            LEFT JOIN midia m_serie ON e.idSerie = m_serie.id
            LEFT JOIN serie s_serie ON e.idSerie = s_serie.id
            ORDER BY m.id ASC;