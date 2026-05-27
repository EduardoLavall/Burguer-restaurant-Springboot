import { useState, type FormEvent } from "react";

import type { ProdutoDados } from "../interfaces/ProdutoDados";
import { useProdutoDadosMutate } from "../hooks/useProdutoDadosMutate";
import "./formularioProduto.css";

export function FormularioProduto() {
  const [nome, setNome] = useState("");
  const [descricao, setDescricao] = useState("");
  const [preco, setPreco] = useState("");
  const [categoria, setCategoria] = useState("comida");
  const [imagem, setImagem] = useState("");
  const [disponibilidade, setDisponibilidade] = useState(true);

  const { mutate, isPending } = useProdutoDadosMutate();

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

    mutate(produto, {
      onSuccess: limparFormulario,
    });
  }

  return (
    <section className="formulario-painel">
      <div className="formulario-painel__cabecalho">
        <div>
          <h2>Novo produto</h2>
          <p>Cadastro via <strong>POST /api/produtos</strong>.</p>
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
          placeholder="Descrição"
          value={descricao}
          onChange={(event) => setDescricao(event.target.value)}
          required
        />

        <div className="formulario__linha">
          <input
            type="number"
            placeholder="Preço"
            min="0"
            step="0.01"
            value={preco}
            onChange={(event) => setPreco(event.target.value)}
            required
          />

          <select value={categoria} onChange={(event) => setCategoria(event.target.value)}>
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
          Produto disponível
        </label>

        <button type="submit" disabled={isPending}>
          {isPending ? "Salvando..." : "Salvar produto"}
        </button>
      </form>
    </section>
  );
}