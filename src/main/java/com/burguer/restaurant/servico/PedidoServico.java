package com.burguer.restaurant.servico;

import java.util.List;

import com.burguer.restaurant.dto.pedido.PedidoRequisicao;
import com.burguer.restaurant.dto.pedido.PedidoResposta;

public interface PedidoServico {

    List<PedidoResposta> listarTodos();

    PedidoResposta criar(PedidoRequisicao requisicao);
}
