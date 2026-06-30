import { useMemo, useState } from "react";
import { useNavigate } from "@tanstack/react-router";

import { useCardapioDados } from "../hooks/produtoHooks";
import { usePedidoCheckout } from "../hooks/pedidoHooks";
import type { CardapioProdutoDados } from "../interfaces/CardapioProdutoDados";
import type { PedidoCheckoutDados } from "../interfaces/PedidoCheckoutDados";
import { formatarCategoria, formatarMoeda } from "../utils/formatadores";

type ItemCarrinho = CardapioProdutoDados & {
  quantidade: number;
};

type CategoriaAgrupada = {
  categoria: string;
  itens: CardapioProdutoDados[];
};

function agruparCardapioPorCategoria(data?: CardapioProdutoDados[]): CategoriaAgrupada[] {
  if (!data) return [];

  // Separei por categoria para a navegacao no tablet ficar mais organizada.
  const grupos = new Map<string, CardapioProdutoDados[]>();

  data.forEach((produto) => {
    const categoriaAtual = grupos.get(produto.categoria) ?? [];
    categoriaAtual.push(produto);
    grupos.set(produto.categoria, categoriaAtual);
  });

  return Array.from(grupos.entries()).map(([categoria, itens]) => ({
    categoria,
    itens,
  }));
}

function calcularResumoCarrinho(carrinho: ItemCarrinho[]) {
  // O frontend mostra uma previa, mas o valor oficial ainda e recalculado no backend.
  const subtotal = carrinho.reduce((acumulador, item) => acumulador + item.preco * item.quantidade, 0);
  const taxaServico = subtotal * 0.1;
  const valorTotal = subtotal + taxaServico;

  return { subtotal, taxaServico, valorTotal };
}

function montarPayloadCheckout(
  nomeCliente: string,
  numeroMesa: string,
  carrinho: ItemCarrinho[],
): PedidoCheckoutDados {
  return {
    nomeCliente: nomeCliente.trim(),
    numeroMesa: Number(numeroMesa),
    itens: carrinho.map((item) => ({
      produtoId: item.id,
      quantidade: item.quantidade,
    })),
  };
}

export function ClienteCardapioPage() {
  const navigate = useNavigate();
  const { data, isLoading, isError, error, refetch, isFetching } = useCardapioDados();
  const pedidoCheckout = usePedidoCheckout();

  const [nomeCliente, setNomeCliente] = useState("");
  const [numeroMesa, setNumeroMesa] = useState("");
  const [carrinho, setCarrinho] = useState<ItemCarrinho[]>([]);

  const cardapioAgrupado = useMemo(() => agruparCardapioPorCategoria(data), [data]);
  const { subtotal, taxaServico, valorTotal } = useMemo(
    () => calcularResumoCarrinho(carrinho),
    [carrinho],
  );

  function adicionarAoCarrinho(produto: CardapioProdutoDados) {
    setCarrinho((itensAtuais) => {
      const itemExistente = itensAtuais.find((item) => item.id === produto.id);

      if (!itemExistente) {
        return [...itensAtuais, { ...produto, quantidade: 1 }];
      }

      return itensAtuais.map((item) =>
        item.id === produto.id ? { ...item, quantidade: item.quantidade + 1 } : item,
      );
    });
  }

  function alterarQuantidade(produtoId: number, quantidade: number) {
    setCarrinho((itensAtuais) =>
      itensAtuais
        .map((item) => (item.id === produtoId ? { ...item, quantidade } : item))
        .filter((item) => item.quantidade > 0),
    );
  }

  function removerDoCarrinho(produtoId: number) {
    setCarrinho((itensAtuais) => itensAtuais.filter((item) => item.id !== produtoId));
  }

  function finalizarPedido() {
    if (carrinho.length === 0) return;

    // Envio so o necessario para a API montar o pedido oficial do restaurante.
    const payload = montarPayloadCheckout(nomeCliente, numeroMesa, carrinho);

    pedidoCheckout.mutate(payload, {
      onSuccess: (pedidoCriado) => {
        setCarrinho([]);
        setNomeCliente("");
        setNumeroMesa("");

        navigate({
          to: "/cliente/pedido/$pedidoId",
          params: { pedidoId: String(pedidoCriado.id) },
        });
      },
    });
  }

  const checkoutDesabilitado =
    carrinho.length === 0 ||
    pedidoCheckout.isPending ||
    nomeCliente.trim().length === 0 ||
    Number(numeroMesa) < 1;

  return (
    <main className="pagina pagina--cliente">
      <section className="painel-banner painel-banner--cliente">
        <div>
          <span className="painel-banner__tag">Autoatendimento</span>
          <h1>Monte seu pedido no tablet</h1>
        </div>

        <div className="painel-banner__acoes">
          <button type="button" onClick={() => refetch()}>
            Atualizar cardapio
          </button>
          {isFetching && !isLoading ? <span className="painel-banner__chip">Atualizando...</span> : null}
        </div>
      </section>

      <section className="layout-cliente">
        <section className="painel">
          <div className="painel__topo">
            <div>
              <h2>Cardapio ativo</h2>
            </div>
          </div>

          {isLoading ? <p className="estado">Carregando cardapio...</p> : null}

          {isError ? (
            <p className="estado estado--erro">
              Erro ao carregar o cardapio.
              {error instanceof Error ? ` ${error.message}` : ""}
            </p>
          ) : null}

          {!isLoading && !isError && cardapioAgrupado.length === 0 ? (
            <p className="estado">Nenhum item ativo disponivel neste momento.</p>
          ) : null}

          <div className="catalogo-cliente">
            {cardapioAgrupado.map((grupo) => (
              <section key={grupo.categoria} className="catalogo-cliente__grupo">
                <div className="catalogo-cliente__cabecalho">
                  <h3>{formatarCategoria(grupo.categoria)}</h3>
                  <span>{grupo.itens.length} itens</span>
                </div>

                <div className="catalogo-cliente__itens">
                  {grupo.itens.map((produto) => {
                    // Se o produto nao tiver imagem, eu uso o placeholder para a grade nao quebrar.
                    const imagem = produto.imagem?.trim() ? produto.imagem : "/placeholder-produto.svg";

                    return (
                      <article key={produto.id} className="produto-cliente">
                        <img src={imagem} alt={`Imagem de ${produto.nome}`} loading="lazy" />

                        <div className="produto-cliente__conteudo">
                          <span className="produto-cliente__categoria">
                            {formatarCategoria(produto.categoria)}
                          </span>
                          <h4>{produto.nome}</h4>
                          <p>{produto.descricao}</p>

                          <div className="produto-cliente__rodape">
                            <strong>{formatarMoeda(produto.preco)}</strong>
                            <button type="button" onClick={() => adicionarAoCarrinho(produto)}>
                              Adicionar
                            </button>
                          </div>
                        </div>
                      </article>
                    );
                  })}
                </div>
              </section>
            ))}
          </div>
        </section>

        <aside className="painel carrinho">
          <div className="painel__topo">
            <div>
              <h2>Seu pedido</h2>
            </div>
          </div>

          <div className="carrinho__formulario">
            <label>
              Nome
              <input
                type="text"
                value={nomeCliente}
                onChange={(event) => setNomeCliente(event.target.value)}
                placeholder="Ex.: Ana"
              />
            </label>

            <label>
              Mesa
              <input
                type="number"
                min="1"
                value={numeroMesa}
                onChange={(event) => setNumeroMesa(event.target.value)}
                placeholder="Ex.: 7"
              />
            </label>
          </div>

          {pedidoCheckout.isError ? (
            <p className="estado estado--erro">
              Nao foi possivel finalizar o pedido.
              {pedidoCheckout.error instanceof Error ? ` ${pedidoCheckout.error.message}` : ""}
            </p>
          ) : null}

          <div className="carrinho__lista">
            {carrinho.length === 0 ? (
              <p className="estado">Seu carrinho esta vazio.</p>
            ) : (
              carrinho.map((item) => (
                <article key={item.id} className="carrinho__item">
                  <div>
                    <strong>{item.nome}</strong>
                    <span>{formatarMoeda(item.preco)}</span>
                  </div>

                  <div className="carrinho__controle">
                    <button type="button" onClick={() => alterarQuantidade(item.id, item.quantidade - 1)}>
                      -
                    </button>
                    <span>{item.quantidade}</span>
                    <button type="button" onClick={() => alterarQuantidade(item.id, item.quantidade + 1)}>
                      +
                    </button>
                    <button type="button" className="carrinho__remover" onClick={() => removerDoCarrinho(item.id)}>
                      Remover
                    </button>
                  </div>
                </article>
              ))
            )}
          </div>

          <div className="resumo-pedido">
            <div>
              <span>Subtotal</span>
              <strong>{formatarMoeda(subtotal)}</strong>
            </div>
            <div>
              <span>Taxa de servico</span>
              <strong>{formatarMoeda(taxaServico)}</strong>
            </div>
            <div className="resumo-pedido__total">
              <span>Total</span>
              <strong>{formatarMoeda(valorTotal)}</strong>
            </div>
          </div>

          <button type="button" className="carrinho__finalizar" disabled={checkoutDesabilitado} onClick={finalizarPedido}>
            {pedidoCheckout.isPending ? "Enviando pedido..." : "Finalizar pedido"}
          </button>
        </aside>
      </section>
    </main>
  );
}
