CREATE TABLE produto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    categoria ENUM('comida', 'bebida', 'acompanhamento', 'doce') NOT NULL,
    disponibilidade BOOLEAN NOT NULL,
    imagem VARCHAR(500) NULL,
    PRIMARY KEY (id)
);
