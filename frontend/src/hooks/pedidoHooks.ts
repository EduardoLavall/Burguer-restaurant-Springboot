import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import type { PedidoCheckoutDados } from "../interfaces/PedidoCheckoutDados";
import type { PedidoDados } from "../interfaces/PedidoDados";
import type { StatusPedido } from "../interfaces/StatusPedido";
import { api } from "../lib/api";
import { queryKeys } from "../lib/queryKeys";

async function buscarPedidos(status?: StatusPedido): Promise<PedidoDados[]> {
  const response = await api.get<PedidoDados[]>("/admin/pedidos", {
    params: status ? { status } : undefined,
  });

  return response.data;
}

async function buscarPedido(id: number): Promise<PedidoDados> {
  const response = await api.get<PedidoDados>(`/pedidos/${id}`);
  return response.data;
}

async function realizarCheckout(payload: PedidoCheckoutDados): Promise<PedidoDados> {
  const response = await api.post<PedidoDados>("/pedidos/checkout", payload);
  return response.data;
}

async function atualizarStatusPedido(id: number, status: StatusPedido) {
  const response = await api.patch(`/admin/pedidos/${id}/status`, {
    status,
  });

  return response.data;
}

async function removerPedido(id: number) {
  await api.delete(`/admin/pedidos/${id}`);
}

export function useAdminPedidos(status?: StatusPedido) {
  return useQuery({
    queryKey: queryKeys.adminPedidos(status),
    queryFn: () => buscarPedidos(status),
    retry: 2,
    refetchInterval: 5000,
  });
}

export function usePedidoDetalhe(id: number) {
  return useQuery({
    queryKey: queryKeys.pedido(id),
    queryFn: () => buscarPedido(id),
    enabled: Number.isFinite(id) && id > 0,
    retry: 2,
    // O cliente reaproveita este endpoint em polling para acompanhar o pedido sem refresh manual.
    refetchInterval: 5000,
  });
}

export function usePedidoCheckout() {
  return useMutation({
    mutationFn: realizarCheckout,
  });
}

export function usePedidoStatusAtualizar() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, status }: { id: number; status: StatusPedido }) => atualizarStatusPedido(id, status),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: queryKeys.adminPedidosBase });
    },
  });
}

export function usePedidoRemover() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: removerPedido,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: queryKeys.adminPedidosBase });
    },
  });
}
