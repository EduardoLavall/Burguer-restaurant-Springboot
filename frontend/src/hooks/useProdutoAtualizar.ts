import axios from "axios";
import { useMutation, useQueryClient } from "@tanstack/react-query";

import type { ProdutoDados } from "../interfaces/ProdutoDados";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function atualizarProduto(id: number, produto: ProdutoDados) {
  const payload = {
    nome: produto.nome,
    descricao: produto.descricao,
    preco: produto.preco,
    categoria: produto.categoria,
    disponibilidade: produto.disponibilidade,
    imagem: produto.imagem,
  };

  const response = await axios.patch(`${API_URL}/api/admin/produtos/${id}`, payload);
  return response.data;
}

export function useProdutoAtualizar() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, produto }: { id: number; produto: ProdutoDados }) => atualizarProduto(id, produto),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admin", "produtos"] });
      await queryClient.invalidateQueries({ queryKey: ["cardapio"] });
    },
  });
}
