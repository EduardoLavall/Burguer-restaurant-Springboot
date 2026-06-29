import axios from "axios";
import { useMutation, useQueryClient } from "@tanstack/react-query";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function atualizarStatusProduto(id: number, disponibilidade: boolean) {
  const response = await axios.patch(`${API_URL}/api/admin/produtos/${id}/status`, {
    disponibilidade,
  });

  return response.data;
}

export function useProdutoStatusAtualizar() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, disponibilidade }: { id: number; disponibilidade: boolean }) =>
      atualizarStatusProduto(id, disponibilidade),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admin", "produtos"] });
      await queryClient.invalidateQueries({ queryKey: ["cardapio"] });
    },
  });
}
