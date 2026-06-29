import { Link, useParams } from "@tanstack/react-router";

import { usePedidoDetalhe } from "../hooks/usePedidoDetalhe";
import type { StatusPedido } from "../interfaces/StatusPedido";

function formatarMoeda(valor: number) {
  return valor.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

function formatarData(valor: string) {
  return new Date(valor).toLocaleString("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  });
}

function formatarStatus(status: string) {
  return status.replace("_", " ");
}

const etapasPedido: StatusPedido[] = ["recebido", "em_preparo", "pronto", "entregue"];

function etapaEstaConcluida(etapa: StatusPedido, statusAtual: StatusPedido) {
  if (statusAtual === "cancelado") return false;
  return etapasPedido.indexOf(etapa) <= etapasPedido.indexOf(statusAtual);
}

export function PedidoConfirmacaoPage() {
  const { pedidoId } = useParams({ from: "/cliente/pedido/$pedidoId" });
  const { data, isLoading, isError, error, isFetching } = usePedidoDetalhe(Number(pedidoId));

  return (
    <main className="pagina pagina--cliente">
      <section className="painel confirmacao">
        <div className="painel__topo">
          <div>
            <h1>Pedido enviado para a cozinha</h1>
            <p>Acompanhe o resumo e o status do seu pedido em tempo real nesta tela.</p>
          </div>
          <div className="confirmacao__acoes">
            <span className="painel__status">
              {isFetching && !isLoading ? "Atualizando status..." : "Acompanhamento automatico ativo"}
            </span>
            <Link to="/cliente" className="shell-app__link">
              Fazer novo pedido
            </Link>
          </div>
        </div>

        {isLoading ? <p className="estado">Buscando dados do pedido...</p> : null}

        {isError ? (
          <p className="estado estado--erro">
            Nao foi possivel consultar o pedido.
            {error instanceof Error ? ` ${error.message}` : ""}
          </p>
        ) : null}

        {data ? (
          <div className="confirmacao__conteudo">
            <section className="confirmacao__destaque">
              <span className={`status status--${data.status}`}>Status: {formatarStatus(data.status)}</span>
              <h2>Pedido #{data.id}</h2>
              <p>
                Mesa <strong>{data.numeroMesa}</strong> em nome de <strong>{data.nomeCliente}</strong>
              </p>
              <small>Criado em {formatarData(data.dataCriacao)}</small>
              <small>Ultima atualizacao em {formatarData(data.dataAtualizacao)}</small>

              {data.status === "cancelado" ? (
                <div className="estado estado--erro">
                  Este pedido foi cancelado pelo restaurante.
                </div>
              ) : (
                <div className="pedido-etapas">
                  {etapasPedido.map((etapa) => (
                    <div
                      key={etapa}
                      className={[
                        "pedido-etapas__item",
                        etapa === data.status ? "is-atual" : "",
                        etapaEstaConcluida(etapa, data.status) ? "is-concluida" : "",
                      ]
                        .filter(Boolean)
                        .join(" ")}
                    >
                      {formatarStatus(etapa)}
                    </div>
                  ))}
                </div>
              )}
            </section>

            <section className="painel">
              <div className="painel__topo">
                <div>
                  <h3>Itens do pedido</h3>
                </div>
              </div>

              <div className="confirmacao__itens">
                {data.itens.map((item) => (
                  <article key={`${item.produtoId}-${item.nomeProduto}`} className="confirmacao__item">
                    <div>
                      <strong>{item.nomeProduto}</strong>
                      <span>{item.quantidade}x</span>
                    </div>
                    <strong>{formatarMoeda(item.subtotal)}</strong>
                  </article>
                ))}
              </div>

              <div className="resumo-pedido">
                <div>
                  <span>Subtotal</span>
                  <strong>{formatarMoeda(data.subtotal)}</strong>
                </div>
                <div>
                  <span>Taxa de servico</span>
                  <strong>{formatarMoeda(data.taxaServico)}</strong>
                </div>
                <div className="resumo-pedido__total">
                  <span>Total</span>
                  <strong>{formatarMoeda(data.valorTotal)}</strong>
                </div>
              </div>
            </section>
          </div>
        ) : null}
      </section>
    </main>
  );
}
