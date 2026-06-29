import axios from "axios";
import { useQuery } from "@tanstack/react-query";

import type { ProdutoDados } from "../interfaces/ProdutoDados";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function buscarProdutos(): Promise<ProdutoDados[]> {
  const response = await axios.get<ProdutoDados[]>(`${API_URL}/api/admin/produtos`);
  return response.data;
}

export function useProdutoDados() {
  return useQuery({
    queryKey: ["admin", "produtos"],
    queryFn: buscarProdutos,
    retry: 2,
  });
}
