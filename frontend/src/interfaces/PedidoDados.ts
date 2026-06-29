import type { StatusPedido } from "./StatusPedido";

export interface PedidoDados {
  id: number;
  nomeCliente: string;
  numeroMesa: number;
  itens: ItemPedidoDados[];
  subtotal: number;
  taxaServico: number;
  valorTotal: number;
  status: StatusPedido;
  dataCriacao: string;
  dataAtualizacao: string;
}

export interface ItemPedidoDados {
  produtoId: number;
  nomeProduto: string;
  quantidade: number;
  precoUnitario: number;
  subtotal: number;
}
