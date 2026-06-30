import type { PedidoDados } from "../interfaces/PedidoDados";
import type { StatusPedido } from "../interfaces/StatusPedido";

export const etapasPedido: StatusPedido[] = ["recebido", "em_preparo", "pronto", "entregue"];

export function proximoStatus(statusAtual: StatusPedido): StatusPedido | null {
  // Esse fluxo representa a ordem normal da operacao da cozinha.
  if (statusAtual === "recebido") return "em_preparo";
  if (statusAtual === "em_preparo") return "pronto";
  if (statusAtual === "pronto") return "entregue";
  return null;
}

export function tituloAcaoStatus(statusAtual: StatusPedido) {
  if (statusAtual === "recebido") return "Marcar em preparo";
  if (statusAtual === "em_preparo") return "Marcar pronto";
  if (statusAtual === "pronto") return "Marcar entregue";
  return "Fluxo encerrado";
}

export function etapaEstaConcluida(etapa: StatusPedido, statusAtual: StatusPedido) {
  if (statusAtual === "cancelado") return false;
  return etapasPedido.indexOf(etapa) <= etapasPedido.indexOf(statusAtual);
}

export function calcularResumoPedidos(pedidos: PedidoDados[]) {
  return pedidos.reduce(
    (acumulador, pedido) => {
      // O resumo do topo serve para o operador bater o volume rapido sem abrir pedido por pedido.
      acumulador.total += 1;
      acumulador[pedido.status] += 1;
      return acumulador;
    },
    {
      total: 0,
      recebido: 0,
      em_preparo: 0,
      pronto: 0,
      entregue: 0,
      cancelado: 0,
    },
  );
}
