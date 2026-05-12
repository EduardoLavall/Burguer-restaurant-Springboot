package com.burguer.restaurant.service;

import java.util.List;

import com.burguer.restaurant.dto.pedido.PedidoRequisicao;
import com.burguer.restaurant.dto.pedido.PedidoResposta;

public interface PedidoService {

    List<PedidoResposta> listarTodos();

    PedidoResposta criar(PedidoRequisicao requisicao);
}
