PRAGMA foreign_keys = ON;

CREATE TABLE produto (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    descricao TEXT NOT NULL,
    preco NUMERIC NOT NULL,
    categoria TEXT NOT NULL,
    disponibilidade INTEGER NOT NULL,
    imagem TEXT NULL
);

CREATE TABLE pedido (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome_cliente TEXT NOT NULL,
    numero_mesa INTEGER NOT NULL,
    subtotal NUMERIC NOT NULL,
    taxa_servico NUMERIC NOT NULL,
    valor_total NUMERIC NOT NULL,
    status TEXT NOT NULL,
    data_criacao TEXT NOT NULL,
    data_atualizacao TEXT NOT NULL
);

CREATE TABLE item_pedido (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pedido_id INTEGER NOT NULL,
    produto_id INTEGER NOT NULL,
    quantidade INTEGER NOT NULL,
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id),
    CONSTRAINT fk_item_pedido_produto FOREIGN KEY (produto_id) REFERENCES produto (id)
);
