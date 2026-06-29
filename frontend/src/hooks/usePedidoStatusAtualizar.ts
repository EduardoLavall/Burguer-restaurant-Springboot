import axios from "axios";
import { useMutation, useQueryClient } from "@tanstack/react-query";

import type { StatusPedido } from "../interfaces/StatusPedido";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function atualizarStatusPedido(id: number, status: StatusPedido) {
  const response = await axios.patch(`${API_URL}/api/admin/pedidos/${id}/status`, {
    status,
  });

  return response.data;
}

export function usePedidoStatusAtualizar() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, status }: { id: number; status: StatusPedido }) => atualizarStatusPedido(id, status),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admin", "pedidos"] });
    },
  });
}
