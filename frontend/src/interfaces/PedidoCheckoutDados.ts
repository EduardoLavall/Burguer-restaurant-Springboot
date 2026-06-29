export interface PedidoCheckoutDados {
  nomeCliente: string;
  numeroMesa: number;
  itens: Array<{
    produtoId: number;
    quantidade: number;
  }>;
}
