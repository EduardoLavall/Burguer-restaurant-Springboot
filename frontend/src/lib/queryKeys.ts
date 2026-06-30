export const queryKeys = {
  cardapio: ["cardapio"] as const,
  adminProdutos: ["admin", "produtos"] as const,
  adminPedidosBase: ["admin", "pedidos"] as const,
  adminPedidos: (status?: string) => ["admin", "pedidos", status ?? "todos"] as const,
  pedido: (id: number) => ["pedido", id] as const,
};
