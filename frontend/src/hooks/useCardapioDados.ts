import axios from "axios";
import { useQuery } from "@tanstack/react-query";

import type { CardapioProdutoDados } from "../interfaces/CardapioProdutoDados";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function buscarCardapio(): Promise<CardapioProdutoDados[]> {
  const response = await axios.get<CardapioProdutoDados[]>(`${API_URL}/api/cardapio`);
  return response.data;
}

export function useCardapioDados() {
  return useQuery({
    queryKey: ["cardapio"],
    queryFn: buscarCardapio,
    retry: 2,
  });
}
