import axios from "axios";
import { useMutation, useQueryClient } from "@tanstack/react-query";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function removerPedido(id: number) {
  await axios.delete(`${API_URL}/api/admin/pedidos/${id}`);
}

export function usePedidoRemover() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: removerPedido,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admin", "pedidos"] });
    },
  });
}
