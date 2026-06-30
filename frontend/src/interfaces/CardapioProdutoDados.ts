import type { CategoriaProduto } from "./CategoriaProduto";

export interface CardapioProdutoDados {
  id: number;
  nome: string;
  descricao: string;
  preco: number;
  categoria: CategoriaProduto;
  imagem: string;
}
