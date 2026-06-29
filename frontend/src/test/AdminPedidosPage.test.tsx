import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { AdminPedidosPage } from "../paginas/AdminPedidosPage";

const useAdminPedidosMock = vi.fn();
const atualizarStatusMock = vi.fn();
const removerPedidoMock = vi.fn();

vi.mock("../hooks/useAdminPedidos", () => ({
  useAdminPedidos: (status?: string) => useAdminPedidosMock(status),
}));

vi.mock("../hooks/usePedidoStatusAtualizar", () => ({
  usePedidoStatusAtualizar: () => ({
    mutate: atualizarStatusMock,
    isPending: false,
  }),
}));

vi.mock("../hooks/usePedidoRemover", () => ({
  usePedidoRemover: () => ({
    mutate: removerPedidoMock,
    isPending: false,
  }),
}));

describe("AdminPedidosPage", () => {
  beforeEach(() => {
    atualizarStatusMock.mockReset();
    removerPedidoMock.mockReset();

    useAdminPedidosMock.mockImplementation((status?: string) => {
      const pedidos = [
        {
          id: 15,
          nomeCliente: "Bruno",
          numeroMesa: 3,
          subtotal: 35,
          taxaServico: 3.5,
          valorTotal: 38.5,
          status: "recebido",
          dataCriacao: "2026-06-16T20:00:00Z",
          dataAtualizacao: "2026-06-16T20:00:00Z",
          itens: [
            {
              produtoId: 1,
              nomeProduto: "Burger",
              quantidade: 1,
              precoUnitario: 35,
              subtotal: 35,
            },
          ],
        },
        {
          id: 21,
          nomeCliente: "Carla",
          numeroMesa: 7,
          subtotal: 24,
          taxaServico: 2.4,
          valorTotal: 26.4,
          status: "cancelado",
          dataCriacao: "2026-06-16T20:10:00Z",
          dataAtualizacao: "2026-06-16T20:15:00Z",
          itens: [
            {
              produtoId: 2,
              nomeProduto: "Batata frita",
              quantidade: 1,
              precoUnitario: 24,
              subtotal: 24,
            },
          ],
        },
      ];

      return {
        data: status ? pedidos.filter((pedido) => pedido.status === status) : pedidos,
        isLoading: false,
        isError: false,
        error: null,
        isFetching: false,
        refetch: vi.fn(),
      };
    });
  });

  it("deve exibir pedido recebido e permitir avancar para em preparo", async () => {
    const usuario = userEvent.setup();

    render(<AdminPedidosPage />);

    expect(screen.getByText("Pedido #15")).toBeInTheDocument();
    expect(screen.getByText("Polling 5s ativo")).toBeInTheDocument();

    await usuario.click(screen.getByRole("button", { name: "Marcar em preparo" }));

    expect(atualizarStatusMock).toHaveBeenCalledWith({
      id: 15,
      status: "em_preparo",
    });
  });

  it("deve permitir excluir pedido cancelado", async () => {
    const usuario = userEvent.setup();

    render(<AdminPedidosPage />);

    await usuario.click(screen.getByRole("button", { name: "Excluir" }));

    expect(removerPedidoMock).toHaveBeenCalledWith(21);
  });
});
