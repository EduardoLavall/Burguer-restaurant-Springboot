ALTER TABLE item_pedido RENAME TO item_pedido_legado;
ALTER TABLE pedido RENAME TO pedido_legado;

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

INSERT INTO pedido (
    id,
    nome_cliente,
    numero_mesa,
    subtotal,
    taxa_servico,
    valor_total,
    status,
    data_criacao,
    data_atualizacao
)
SELECT
    id,
    'Cliente legado',
    0,
    valor_total,
    0.00,
    valor_total,
    CASE
        WHEN status = 'PENDENTE' THEN 'recebido'
        WHEN status = 'EM_ANDAMENTO' THEN 'em_preparo'
        WHEN status = 'CONCLUIDO' THEN 'entregue'
        WHEN status = 'CANCELADO' THEN 'cancelado'
        ELSE 'recebido'
    END,
    data_pedido,
    COALESCE(data_entrega, data_pedido)
FROM pedido_legado;

INSERT INTO item_pedido (id, pedido_id, produto_id, quantidade)
SELECT id, pedido_id, produto_id, quantidade
FROM item_pedido_legado;

DROP TABLE item_pedido_legado;
DROP TABLE pedido_legado;
