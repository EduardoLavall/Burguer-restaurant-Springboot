import axios from "axios";
import { useMutation } from "@tanstack/react-query";

import type { PedidoCheckoutDados } from "../interfaces/PedidoCheckoutDados";
import type { PedidoDados } from "../interfaces/PedidoDados";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function realizarCheckout(payload: PedidoCheckoutDados): Promise<PedidoDados> {
  const response = await axios.post<PedidoDados>(`${API_URL}/api/pedidos/checkout`, payload);
  return response.data;
}

export function usePedidoCheckout() {
  return useMutation({
    mutationFn: realizarCheckout,
  });
}
