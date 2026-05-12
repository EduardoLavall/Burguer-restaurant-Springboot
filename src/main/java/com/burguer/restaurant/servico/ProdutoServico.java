package com.burguer.restaurant.servico;

import java.util.List;

import com.burguer.restaurant.dto.produto.ProdutoRequisicao;
import com.burguer.restaurant.dto.produto.ProdutoResposta;

public interface ProdutoServico {

    List<ProdutoResposta> listarTodos();

    ProdutoResposta criar(ProdutoRequisicao requisicao);
}
