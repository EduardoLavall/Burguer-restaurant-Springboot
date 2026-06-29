import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { ClienteCardapioPage } from "../paginas/ClienteCardapioPage";

const navigateMock = vi.fn();
const mutateMock = vi.fn();

const useCardapioDadosMock = vi.fn();
const usePedidoCheckoutMock = vi.fn();

vi.mock("@tanstack/react-router", () => ({
  useNavigate: () => navigateMock,
}));

vi.mock("../hooks/useCardapioDados", () => ({
  useCardapioDados: () => useCardapioDadosMock(),
}));

vi.mock("../hooks/usePedidoCheckout", () => ({
  usePedidoCheckout: () => usePedidoCheckoutMock(),
}));

describe("ClienteCardapioPage", () => {
  beforeEach(() => {
    navigateMock.mockReset();
    mutateMock.mockReset();

    useCardapioDadosMock.mockReturnValue({
      data: [
        {
          id: 1,
          nome: "Burger Smash",
          descricao: "Pao, carne e queijo",
          preco: 28.9,
          categoria: "comida",
          imagem: "",
        },
      ],
      isLoading: false,
      isError: false,
      error: null,
      refetch: vi.fn(),
      isFetching: false,
    });

    usePedidoCheckoutMock.mockReturnValue({
      mutate: mutateMock,
      isPending: false,
      isError: false,
      error: null,
    });
  });

  it("deve adicionar item ao carrinho e enviar checkout com mesa e nome", async () => {
    const usuario = userEvent.setup();

    render(<ClienteCardapioPage />);

    await usuario.click(screen.getByRole("button", { name: "Adicionar" }));

    expect(screen.getAllByText("Burger Smash")).toHaveLength(2);
    expect(screen.getByText(/Subtotal/i)).toBeInTheDocument();
    expect(screen.getByText(/Taxa de servico/i)).toBeInTheDocument();

    await usuario.type(screen.getByLabelText("Nome"), "Carla");
    await usuario.type(screen.getByLabelText("Mesa"), "9");
    await usuario.click(screen.getByRole("button", { name: "Finalizar pedido" }));

    expect(mutateMock).toHaveBeenCalledWith(
      {
        nomeCliente: "Carla",
        numeroMesa: 9,
        itens: [{ produtoId: 1, quantidade: 1 }],
      },
      expect.objectContaining({
        onSuccess: expect.any(Function),
      }),
    );
  });
});
