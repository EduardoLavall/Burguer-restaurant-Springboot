import type { CategoriaProduto } from "./CategoriaProduto";

export interface ProdutoDados {
  id?: number;
  nome: string;
  descricao: string;
  preco: number;
  categoria: CategoriaProduto;
  disponibilidade: boolean;
  imagem: string;
}
