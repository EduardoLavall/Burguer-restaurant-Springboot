import { useMemo, useState } from "react";

import { useAdminPedidos } from "../hooks/useAdminPedidos";
import { usePedidoRemover } from "../hooks/usePedidoRemover";
import { usePedidoStatusAtualizar } from "../hooks/usePedidoStatusAtualizar";
import type { PedidoDados } from "../interfaces/PedidoDados";
import type { StatusPedido } from "../interfaces/StatusPedido";

const filtrosStatus: Array<{ rotulo: string; valor: StatusPedido | "todos" }> = [
  { rotulo: "Todos", valor: "todos" },
  { rotulo: "Recebidos", valor: "recebido" },
  { rotulo: "Em preparo", valor: "em_preparo" },
  { rotulo: "Prontos", valor: "pronto" },
  { rotulo: "Entregues", valor: "entregue" },
  { rotulo: "Cancelados", valor: "cancelado" },
];

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

function proximoStatus(statusAtual: StatusPedido): StatusPedido | null {
  if (statusAtual === "recebido") return "em_preparo";
  if (statusAtual === "em_preparo") return "pronto";
  if (statusAtual === "pronto") return "entregue";
  return null;
}

function tituloAcao(statusAtual: StatusPedido) {
  if (statusAtual === "recebido") return "Marcar em preparo";
  if (statusAtual === "em_preparo") return "Marcar pronto";
  if (statusAtual === "pronto") return "Marcar entregue";
  return "Fluxo encerrado";
}

function calcularResumo(pedidos: PedidoDados[]) {
  return pedidos.reduce(
    (acumulador, pedido) => {
      acumulador.total += 1;
      acumulador[pedido.status] += 1;
      return acumulador;
    },
    {
      total: 0,
      recebido: 0,
      em_preparo: 0,
      pronto: 0,
      entregue: 0,
      cancelado: 0,
    },
  );
}

export function AdminPedidosPage() {
  const [filtroSelecionado, setFiltroSelecionado] = useState<StatusPedido | "todos">("todos");
  const { data, isLoading, isError, error, isFetching, refetch } = useAdminPedidos(
    filtroSelecionado === "todos" ? undefined : filtroSelecionado,
  );
  const atualizarStatus = usePedidoStatusAtualizar();
  const removerPedido = usePedidoRemover();

  const resumo = useMemo(() => calcularResumo(data ?? []), [data]);

  function alterarStatus(pedidoId: number, status: StatusPedido) {
    atualizarStatus.mutate({ id: pedidoId, status });
  }

  function excluirPedido(pedidoId: number) {
    removerPedido.mutate(pedidoId);
  }

  return (
    <main className="pagina pagina--admin">
      <section className="painel-banner">
        <div>
          <span className="painel-banner__tag">Operacao do restaurante</span>
          <h1>Painel de pedidos em tempo real por polling</h1>
          <p>
            Novos pedidos do tablet aparecem automaticamente aqui a cada 5 segundos, com prioridade
            para os nao finalizados.
          </p>
        </div>

        <div className="painel-banner__acoes">
          <button type="button" onClick={() => refetch()}>
            Atualizar agora
          </button>
          <span className="painel-banner__chip">{isFetching ? "Sincronizando..." : "Polling 5s ativo"}</span>
        </div>
      </section>

      <section className="painel painel--resumo">
        <div className="resumo-operacao">
          <article>
            <span>Total</span>
            <strong>{resumo.total}</strong>
          </article>
          <article>
            <span>Recebidos</span>
            <strong>{resumo.recebido}</strong>
          </article>
          <article>
            <span>Em preparo</span>
            <strong>{resumo.em_preparo}</strong>
          </article>
          <article>
            <span>Prontos</span>
            <strong>{resumo.pronto}</strong>
          </article>
        </div>
      </section>

      <section className="painel">
        <div className="painel__topo">
          <div>
            <h2>Fila operacional</h2>
            <p>
              Consumo de <strong>GET /api/admin/pedidos</strong> com filtro opcional por status.
            </p>
          </div>
        </div>

        <div className="filtros-status">
          {filtrosStatus.map((filtro) => (
            <button
              key={filtro.valor}
              type="button"
              className={filtroSelecionado === filtro.valor ? "filtros-status__botao is-ativo" : "filtros-status__botao"}
              onClick={() => setFiltroSelecionado(filtro.valor)}
            >
              {filtro.rotulo}
            </button>
          ))}
        </div>

        {isLoading ? <p className="estado">Carregando pedidos...</p> : null}

        {isError ? (
          <p className="estado estado--erro">
            Erro ao buscar pedidos.
            {error instanceof Error ? ` ${error.message}` : ""}
          </p>
        ) : null}

        {!isLoading && !isError && data?.length === 0 ? (
          <p className="estado">Nenhum pedido encontrado para o filtro atual.</p>
        ) : null}

        <section className="grade-pedidos">
          {data?.map((pedido) => {
            const statusSeguinte = proximoStatus(pedido.status);

            return (
              <article key={pedido.id} className={`pedido-card pedido-card--${pedido.status}`}>
                <header className="pedido-card__topo">
                  <div>
                    <span className={`status status--${pedido.status}`}>{formatarStatus(pedido.status)}</span>
                    <h3>Pedido #{pedido.id}</h3>
                  </div>

                  <div className="pedido-card__meta">
                    <strong>Mesa {pedido.numeroMesa}</strong>
                    <span>{pedido.nomeCliente}</span>
                  </div>
                </header>

                <div className="pedido-card__horarios">
                  <span>Criado: {formatarData(pedido.dataCriacao)}</span>
                  <span>Atualizado: {formatarData(pedido.dataAtualizacao)}</span>
                </div>

                <div className="pedido-card__itens">
                  {pedido.itens.map((item) => (
                    <div key={`${pedido.id}-${item.produtoId}-${item.nomeProduto}`} className="pedido-card__item">
                      <span>
                        {item.quantidade}x {item.nomeProduto}
                      </span>
                      <strong>{formatarMoeda(item.subtotal)}</strong>
                    </div>
                  ))}
                </div>

                <div className="pedido-card__total">
                  <span>Total</span>
                  <strong>{formatarMoeda(pedido.valorTotal)}</strong>
                </div>

                <div className="pedido-card__acoes">
                  <button
                    type="button"
                    disabled={!statusSeguinte || atualizarStatus.isPending || removerPedido.isPending}
                    onClick={() => statusSeguinte && alterarStatus(pedido.id, statusSeguinte)}
                  >
                    {tituloAcao(pedido.status)}
                  </button>

                  {pedido.status !== "cancelado" && pedido.status !== "entregue" ? (
                    <button
                      type="button"
                      className="pedido-card__cancelar"
                      disabled={atualizarStatus.isPending || removerPedido.isPending}
                      onClick={() => alterarStatus(pedido.id, "cancelado")}
                    >
                      Cancelar
                    </button>
                  ) : null}

                  {pedido.status === "cancelado" || pedido.status === "entregue" ? (
                    <button
                      type="button"
                      className="pedido-card__cancelar"
                      disabled={atualizarStatus.isPending || removerPedido.isPending}
                      onClick={() => excluirPedido(pedido.id)}
                    >
                      Excluir
                    </button>
                  ) : null}
                </div>
              </article>
            );
          })}
        </section>
      </section>
    </main>
  );
}
