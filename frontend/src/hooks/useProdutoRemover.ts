import axios from "axios";
import { useMutation, useQueryClient } from "@tanstack/react-query";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function removerProduto(id: number) {
  await axios.delete(`${API_URL}/api/admin/produtos/${id}`);
}

export function useProdutoRemover() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => removerProduto(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admin", "produtos"] });
      await queryClient.invalidateQueries({ queryKey: ["cardapio"] });
    },
  });
}
