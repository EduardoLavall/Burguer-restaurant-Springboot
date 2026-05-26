import type { ProdutoDados } from "../interfaces/ProdutoDados";
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

        <div className="cartao-produto__rodape">
          <strong>{formatarPreco(produto.preco)}</strong>
        </div>
      </div>
    </article>
  );
}
