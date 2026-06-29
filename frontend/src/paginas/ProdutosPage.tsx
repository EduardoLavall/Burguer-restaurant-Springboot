import { useState } from "react";

import { CartaoProduto } from "../componentes/CartaoProduto";
import { FormularioProduto } from "../componentes/FormularioProduto";
import { Modal } from "../componentes/Modal";
import { useProdutoDados } from "../hooks/useProdutoDados";

export function ProdutosPage() {
  const { data, isLoading, isError, error, refetch, isFetching } = useProdutoDados();
  const [modalAberto, setModalAberto] = useState(false);

  return (
    <main className="pagina pagina--admin">
      <section className="painel-banner">
        <div>
          <span className="painel-banner__tag">Area administrativa</span>
          <h1>Gestao de produtos da hamburgueria</h1>
          <p>
            Cadastre, edite e controle o que fica visivel no tablet do cliente sem misturar o
            fluxo de operacao com o fluxo de compra.
          </p>
        </div>

        <div className="painel-banner__acoes">
          <button type="button" onClick={() => refetch()}>
            Atualizar lista
          </button>
          <button type="button" className="painel-banner__acao-primaria" onClick={() => setModalAberto(true)}>
            Novo produto
          </button>
        </div>
      </section>

      <Modal isOpen={modalAberto} onClose={() => setModalAberto(false)} title="Novo produto">
        <FormularioProduto onSuccess={() => setModalAberto(false)} />
      </Modal>

      <section className="painel">
        <div className="painel__topo">
          <div>
            <h2>Catalogo administrativo</h2>
            <p>
              Consumo de <strong>GET /api/admin/produtos</strong> com edicao e controle de
              disponibilidade.
            </p>
          </div>

          {isFetching && !isLoading ? <span className="painel__status">Sincronizando...</span> : null}
        </div>

        {isLoading ? <p className="estado">Carregando produtos...</p> : null}

        {isError ? (
          <p className="estado estado--erro">
            Erro ao buscar produtos.
            {error instanceof Error ? ` ${error.message}` : ""}
          </p>
        ) : null}

        {!isLoading && !isError && data?.length === 0 ? (
          <p className="estado">Nenhum produto cadastrado no momento.</p>
        ) : null}

        <section className="grade-produtos">
          {data?.map((produto) => (
            <CartaoProduto key={produto.id ?? `${produto.nome}-${produto.preco}`} produto={produto} />
          ))}
        </section>
      </section>
    </main>
  );
}
