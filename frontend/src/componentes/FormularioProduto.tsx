import { useEffect, useState, type FormEvent } from "react";

import { useProdutoAtualizar, useProdutoCriar } from "../hooks/produtoHooks";
import type { CategoriaProduto } from "../interfaces/CategoriaProduto";
import type { ProdutoDados } from "../interfaces/ProdutoDados";
import "./formularioProduto.css";

type FormularioProdutoProps = {
  onSuccess?: () => void;
  initial?: ProdutoDados;
};

export function FormularioProduto({ onSuccess, initial }: FormularioProdutoProps) {
  const [nome, setNome] = useState("");
  const [descricao, setDescricao] = useState("");
  const [preco, setPreco] = useState("");
  const [categoria, setCategoria] = useState<CategoriaProduto>("comida");
  const [imagem, setImagem] = useState("");
  const [disponibilidade, setDisponibilidade] = useState(true);

  const { mutate: criar, isPending: criando } = useProdutoCriar();
  const { mutate: atualizar, isPending: atualizando } = useProdutoAtualizar();

  useEffect(() => {
    if (!initial) return;
    setNome(initial.nome ?? "");
    setDescricao(initial.descricao ?? "");
    setPreco(String(initial.preco ?? ""));
    setCategoria(initial.categoria ?? "comida");
    setImagem(initial.imagem ?? "");
    setDisponibilidade(initial.disponibilidade ?? true);
  }, [initial]);

  function limparFormulario() {
    setNome("");
    setDescricao("");
    setPreco("");
    setCategoria("comida");
    setImagem("");
    setDisponibilidade(true);
  }

  function enviarFormulario(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const produto: ProdutoDados = {
      nome: nome.trim(),
      descricao: descricao.trim(),
      preco: Number(preco),
      categoria,
      imagem: imagem.trim(),
      disponibilidade,
    };

    if (initial?.id) {
      atualizar(
        { id: initial.id, produto },
        {
          onSuccess: () => {
            limparFormulario();
            onSuccess?.();
          },
        },
      );
      return;
    }

    criar(produto, {
      onSuccess: () => {
        limparFormulario();
        onSuccess?.();
      },
    });
  }

  return (
    <section className="formulario-painel">
      <div className="formulario-painel__cabecalho">
        <div>
          <h2>{initial?.id ? "Editar produto" : "Novo produto"}</h2>
          <p>
            {initial?.id ? (
              <>
                Edicao via <strong>PATCH /api/admin/produtos/{initial.id}</strong>.
              </>
            ) : (
              <>
                Cadastro via <strong>POST /api/admin/produtos</strong>.
              </>
            )}
          </p>
        </div>
      </div>

      <form className="formulario" onSubmit={enviarFormulario}>
        <input
          type="text"
          placeholder="Nome"
          value={nome}
          onChange={(event) => setNome(event.target.value)}
          required
        />

        <textarea
          placeholder="Descricao"
          value={descricao}
          onChange={(event) => setDescricao(event.target.value)}
          required
        />

        <div className="formulario__linha">
          <input
            type="number"
            placeholder="Preco"
            min="0"
            step="0.01"
            value={preco}
            onChange={(event) => setPreco(event.target.value)}
            required
          />

          <select value={categoria} onChange={(event) => setCategoria(event.target.value as CategoriaProduto)}>
            <option value="comida">comida</option>
            <option value="bebida">bebida</option>
            <option value="acompanhamento">acompanhamento</option>
            <option value="doce">doce</option>
          </select>
        </div>

        <input
          type="url"
          placeholder="URL da imagem"
          value={imagem}
          onChange={(event) => setImagem(event.target.value)}
        />

        <label className="formulario__checkbox">
          <input
            type="checkbox"
            checked={disponibilidade}
            onChange={(event) => setDisponibilidade(event.target.checked)}
          />
          Produto ativo
        </label>

        <button type="submit" disabled={criando || atualizando}>
          {atualizando
            ? "Atualizando..."
            : criando
              ? "Salvando..."
              : initial?.id
                ? "Salvar alteracoes"
                : "Salvar produto"}
        </button>
      </form>
    </section>
  );
}
