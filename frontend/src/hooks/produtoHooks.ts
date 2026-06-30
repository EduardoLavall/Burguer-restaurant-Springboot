import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import type { CardapioProdutoDados } from "../interfaces/CardapioProdutoDados";
import type { ProdutoDados } from "../interfaces/ProdutoDados";
import { api } from "../lib/api";
import { queryKeys } from "../lib/queryKeys";

async function buscarProdutos(): Promise<ProdutoDados[]> {
  const response = await api.get<ProdutoDados[]>("/admin/produtos");
  return response.data;
}

async function buscarCardapio(): Promise<CardapioProdutoDados[]> {
  const response = await api.get<CardapioProdutoDados[]>("/cardapio");
  return response.data;
}

async function criarProduto(produto: ProdutoDados) {
  const response = await api.post("/admin/produtos", produto);
  return response.data;
}

async function atualizarProduto(id: number, produto: ProdutoDados) {
  const response = await api.patch(`/admin/produtos/${id}`, produto);
  return response.data;
}

async function atualizarStatusProduto(id: number, disponibilidade: boolean) {
  const response = await api.patch(`/admin/produtos/${id}/status`, {
    disponibilidade,
  });

  return response.data;
}

async function removerProduto(id: number) {
  await api.delete(`/admin/produtos/${id}`);
}

export function useProdutoDados() {
  return useQuery({
    queryKey: queryKeys.adminProdutos,
    queryFn: buscarProdutos,
    retry: 2,
  });
}

export function useCardapioDados() {
  return useQuery({
    queryKey: queryKeys.cardapio,
    queryFn: buscarCardapio,
    retry: 2,
  });
}

export function useProdutoCriar() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: criarProduto,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: queryKeys.adminProdutos });
      await queryClient.invalidateQueries({ queryKey: queryKeys.cardapio });
    },
  });
}

export function useProdutoAtualizar() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, produto }: { id: number; produto: ProdutoDados }) => atualizarProduto(id, produto),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: queryKeys.adminProdutos });
      await queryClient.invalidateQueries({ queryKey: queryKeys.cardapio });
    },
  });
}

export function useProdutoStatusAtualizar() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, disponibilidade }: { id: number; disponibilidade: boolean }) =>
      atualizarStatusProduto(id, disponibilidade),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: queryKeys.adminProdutos });
      await queryClient.invalidateQueries({ queryKey: queryKeys.cardapio });
    },
  });
}

export function useProdutoRemover() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: removerProduto,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: queryKeys.adminProdutos });
      await queryClient.invalidateQueries({ queryKey: queryKeys.cardapio });
    },
  });
}
