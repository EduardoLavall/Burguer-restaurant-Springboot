CREATE TABLE cliente (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    endereco_entrega VARCHAR(255) NOT NULL,
    preferencia_pagamento ENUM('cartao', 'pix') NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_cliente_email UNIQUE (email)
);
