import { useState } from "react";
import type { ProdutoDados } from "../interfaces/ProdutoDados";
import { Modal } from "./Modal";
import { FormularioProduto } from "./FormularioProduto";
import { useProdutoRemover } from "../hooks/useProdutoRemover";
import { useProdutoAtualizar } from "../hooks/useProdutoAtualizar";
import "./cartaoProduto.css";

interface CartaoProdutoProps {
  produto: ProdutoDados;
}

function formatarPreco(valor: number) {
  return valor.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

export function CartaoProduto({ produto }: CartaoProdutoProps) {
  const imagem = produto.imagem?.trim() ? produto.imagem : "/placeholder-produto.svg";
  const statusDisponivel = produto.disponibilidade ? "Disponível" : "Indisponível";

  const [confirmAberto, setConfirmAberto] = useState(false);
  const [visualizarAberto, setVisualizarAberto] = useState(false);
  const [editarAberto, setEditarAberto] = useState(false);

  const remover = useProdutoRemover();
  const atualizar = useProdutoAtualizar();

  function aoConfirmarRemover() {
    if (produto.id == null) return;
    remover.mutate(produto.id, { onSuccess: () => setConfirmAberto(false) });
  }

  function alternarDisponibilidade(checked: boolean) {
    if (produto.id == null) return;
    const atualizado: ProdutoDados = { ...produto, disponibilidade: checked };
    atualizar.mutate({ id: produto.id, produto: atualizado });
  }

  return (
    <article className={`cartao-produto ${produto.disponibilidade ? "is-disponivel" : "is-indisponivel"}`}>
      <div className="cartao-produto__imagem">
        <img src={imagem} alt={`Imagem de ${produto.nome}`} loading="lazy" />
      </div>

      <div className="cartao-produto__conteudo">
        <div className="cartao-produto__cabecalho">
          <span className="cartao-produto__categoria">{produto.categoria}</span>
          <span className="cartao-produto__status">{statusDisponivel}</span>
        </div>

        <h2>{produto.nome}</h2>
        <p>{produto.descricao}</p>

        <div className="cartao-produto__acoes">
          <label className="cartao-produto__checkbox">
            <input
              type="checkbox"
              checked={produto.disponibilidade}
              onChange={(e) => alternarDisponibilidade(e.target.checked)}
            />
            Disponível
          </label>

          <div className="cartao-produto__botoes">
            <button className="btn-icon" title="Visualizar" onClick={() => setVisualizarAberto(true)}>
              👁️
            </button>

            <button className="btn-icon" title="Editar" onClick={() => setEditarAberto(true)}>
              ✏️
            </button>

            <button className="btn-icon btn-danger" title="Excluir" onClick={() => setConfirmAberto(true)}>
              ×
            </button>
          </div>
        </div>

        <div className="cartao-produto__rodape">
          <strong>{formatarPreco(produto.preco)}</strong>
        </div>
      </div>

      <Modal isOpen={confirmAberto} onClose={() => setConfirmAberto(false)} title="Confirmar exclusão">
        <p>Tem certeza que deseja marcar este produto como indisponível?</p>
        <div style={{ display: "flex", gap: 8, marginTop: 12 }}>
          <button onClick={aoConfirmarRemover} className="btn-danger">Confirmar</button>
          <button onClick={() => setConfirmAberto(false)}>Cancelar</button>
        </div>
      </Modal>

      <Modal isOpen={visualizarAberto} onClose={() => setVisualizarAberto(false)} title="Detalhes do produto">
        <div>
          <img src={imagem} alt={`Imagem de ${produto.nome}`} style={{ maxWidth: "100%", marginBottom: 12 }} />
          <h3>{produto.nome}</h3>
          <p><strong>Categoria:</strong> {produto.categoria}</p>
          <p><strong>Preço:</strong> {formatarPreco(produto.preco)}</p>
          <p><strong>Disponibilidade:</strong> {produto.disponibilidade ? "Disponível" : "Indisponível"}</p>
          <p>{produto.descricao}</p>
        </div>
      </Modal>

      <Modal isOpen={editarAberto} onClose={() => setEditarAberto(false)} title="Editar produto">
        <FormularioProduto initial={produto} onSuccess={() => setEditarAberto(false)} />
      </Modal>
    </article>
  );
}
