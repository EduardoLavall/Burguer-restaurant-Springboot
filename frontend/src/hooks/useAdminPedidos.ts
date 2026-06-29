import axios from "axios";
import { useQuery } from "@tanstack/react-query";

import type { PedidoDados } from "../interfaces/PedidoDados";
import type { StatusPedido } from "../interfaces/StatusPedido";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function buscarPedidos(status?: StatusPedido): Promise<PedidoDados[]> {
  const response = await axios.get<PedidoDados[]>(`${API_URL}/api/admin/pedidos`, {
    params: status ? { status } : undefined,
  });

  return response.data;
}

export function useAdminPedidos(status?: StatusPedido) {
  return useQuery({
    queryKey: ["admin", "pedidos", status ?? "todos"],
    queryFn: () => buscarPedidos(status),
    retry: 2,
    refetchInterval: 5000,
  });
}
