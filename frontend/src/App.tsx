import { useState } from "react";
import { CartaoProduto } from "./componentes/CartaoProduto";
import { FormularioProduto } from "./componentes/FormularioProduto";
import { Modal } from "./componentes/Modal";
import { useProdutoDados } from "./hooks/useProdutoDados";
import "./App.css";

function App() {
  const { data, isLoading, isError, error, refetch, isFetching } = useProdutoDados();
  const [modalAberto, setModalAberto] = useState(false);

  return (
    <main className="pagina">
      <section className="hero">
        <div className="hero__texto">
          <span className="hero__tag">MenuStream • Aula 10</span>
          <h1>Catálogo de produtos do restaurante</h1>
          <p>
            Interface inicial para exibir os produtos cadastrados no backend Spring Boot em cartões
            visuais, com busca via React Query e Axios.
          </p>
        </div>

        <div className="hero__acoes">
          <button className="hero__acao" type="button" onClick={() => refetch()}>
            Atualizar catálogo
          </button>

          <button
            className="hero__acao hero__acao--novo"
            type="button"
            onClick={() => setModalAberto(true)}
          >
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
            <h2>Produtos</h2>
            <p>Listagem consumindo <strong>GET /api/produtos</strong>.</p>
          </div>

          {isFetching && !isLoading ? <span className="painel__status">Atualizando dados...</span> : null}
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

export default App;
