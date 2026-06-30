import axios from "axios";
import { useState } from "react";

import type { ProdutoDados } from "../interfaces/ProdutoDados";
import { useProdutoRemover, useProdutoStatusAtualizar } from "../hooks/produtoHooks";
import { Modal } from "./Modal";
import { FormularioProduto } from "./FormularioProduto";
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
  const statusDisponivel = produto.disponibilidade ? "Ativo" : "Inativo";

  const [confirmAberto, setConfirmAberto] = useState(false);
  const [visualizarAberto, setVisualizarAberto] = useState(false);
  const [editarAberto, setEditarAberto] = useState(false);

  const remover = useProdutoRemover();
  const atualizarStatus = useProdutoStatusAtualizar();

  function abrirConfirmacaoRemover() {
    remover.reset();
    setConfirmAberto(true);
  }

  function aoConfirmarRemover() {
    if (produto.id == null) return;
    remover.mutate(produto.id, { onSuccess: () => setConfirmAberto(false) });
  }

  function alternarDisponibilidade(checked: boolean) {
    if (produto.id == null) return;
    atualizarStatus.mutate({ id: produto.id, disponibilidade: checked });
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
            Visivel no cliente
          </label>

          <div className="cartao-produto__botoes">
            <button className="btn-icon" title="Visualizar" type="button" onClick={() => setVisualizarAberto(true)}>
              VER
            </button>

            <button className="btn-icon" title="Editar" type="button" onClick={() => setEditarAberto(true)}>
              EDIT
            </button>

            <button className="btn-icon btn-danger" title="Excluir" type="button" onClick={abrirConfirmacaoRemover}>
              EXCLUIR
            </button>
          </div>
        </div>

        <div className="cartao-produto__rodape">
          <strong>{formatarPreco(produto.preco)}</strong>
        </div>
      </div>

      <Modal
        isOpen={confirmAberto}
        onClose={() => {
          remover.reset();
          setConfirmAberto(false);
        }}
        title="Excluir produto"
      >
        <p>Este produto sera excluido do painel e do cardapio do cliente.</p>

        {remover.isError ? (
          <p className="cartao-produto__erro">
            {axios.isAxiosError(remover.error) && remover.error.response?.data?.message
              ? remover.error.response.data.message
              : "Nao foi possivel excluir este produto agora."}
          </p>
        ) : null}

        <div className="janela-acoes">
          <button
            onClick={aoConfirmarRemover}
            className="janela-acao janela-acao--danger"
            type="button"
            disabled={remover.isPending}
          >
            Confirmar
          </button>
          <button
            onClick={() => {
              remover.reset();
              setConfirmAberto(false);
            }}
            className="janela-acao"
            type="button"
          >
            Cancelar
          </button>
        </div>
      </Modal>

      <Modal isOpen={visualizarAberto} onClose={() => setVisualizarAberto(false)} title="Detalhes do produto">
        <div className="janela-visualizacao">
          <img src={imagem} alt={`Imagem de ${produto.nome}`} />
          <h3>{produto.nome}</h3>
          <p>
            <strong>Categoria:</strong> {produto.categoria}
          </p>
          <p>
            <strong>Preco:</strong> {formatarPreco(produto.preco)}
          </p>
          <p>
            <strong>Status:</strong> {produto.disponibilidade ? "Ativo" : "Inativo"}
          </p>
          <p>{produto.descricao}</p>
        </div>
      </Modal>

      <Modal isOpen={editarAberto} onClose={() => setEditarAberto(false)} title="Editar produto">
        <FormularioProduto initial={produto} onSuccess={() => setEditarAberto(false)} />
      </Modal>
    </article>
  );
}
