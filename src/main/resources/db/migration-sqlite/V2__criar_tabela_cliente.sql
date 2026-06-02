CREATE TABLE cliente (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    email TEXT NOT NULL,
    endereco_entrega TEXT NOT NULL,
    preferencia_pagamento TEXT NOT NULL,
    CONSTRAINT uk_cliente_email UNIQUE (email)
);