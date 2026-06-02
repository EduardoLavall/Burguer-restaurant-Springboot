CREATE TABLE pedido (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id INTEGER NOT NULL,
    valor_total NUMERIC NOT NULL DEFAULT 0.00,
    status TEXT NOT NULL,
    data_pedido TEXT NOT NULL,
    data_entrega TEXT NULL,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id)
);