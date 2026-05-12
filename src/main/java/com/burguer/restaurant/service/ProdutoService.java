package com.burguer.restaurant.service;

import java.util.List;

import com.burguer.restaurant.dto.produto.ProdutoPrecoRequisicao;
import com.burguer.restaurant.dto.produto.ProdutoRequisicao;
import com.burguer.restaurant.dto.produto.ProdutoResposta;

public interface ProdutoService {

    List<ProdutoResposta> listarTodos();

    ProdutoResposta criar(ProdutoRequisicao requisicao);

    ProdutoResposta alterarPreco(Long id, ProdutoPrecoRequisicao requisicao);

    void remover(Long id);
}
