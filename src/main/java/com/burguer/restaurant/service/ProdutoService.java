package com.burguer.restaurant.service;

import java.util.List;

import com.burguer.restaurant.dto.cardapio.CardapioProdutoResposta;
import com.burguer.restaurant.dto.produto.ProdutoPrecoRequisicao;
import com.burguer.restaurant.dto.produto.ProdutoRequisicao;
import com.burguer.restaurant.dto.produto.ProdutoResposta;
import com.burguer.restaurant.dto.produto.ProdutoStatusRequisicao;

public interface ProdutoService {

    List<ProdutoResposta> listarTodos();

    List<CardapioProdutoResposta> listarCardapio();

    ProdutoResposta criar(ProdutoRequisicao requisicao);

    ProdutoResposta alterarPreco(Long id, ProdutoPrecoRequisicao requisicao);

    ProdutoResposta atualizar(Long id, ProdutoRequisicao requisicao);

    ProdutoResposta atualizarStatus(Long id, ProdutoStatusRequisicao requisicao);

    void remover(Long id);
}
