import axios from "axios";
import { useQuery } from "@tanstack/react-query";

import type { PedidoDados } from "../interfaces/PedidoDados";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function buscarPedido(id: number): Promise<PedidoDados> {
  const response = await axios.get<PedidoDados>(`${API_URL}/api/pedidos/${id}`);
  return response.data;
}

export function usePedidoDetalhe(id: number) {
  return useQuery({
    queryKey: ["pedido", id],
    queryFn: () => buscarPedido(id),
    enabled: Number.isFinite(id) && id > 0,
    retry: 2,
    // O cliente reaproveita este endpoint em polling para acompanhar o pedido sem refresh manual.
    refetchInterval: 5000,
  });
}
