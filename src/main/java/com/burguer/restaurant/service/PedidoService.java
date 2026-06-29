package com.burguer.restaurant.service;

import java.util.List;

import com.burguer.restaurant.dominio.pedido.StatusPedido;
import com.burguer.restaurant.dto.pedido.PedidoCheckoutRequisicao;
import com.burguer.restaurant.dto.pedido.PedidoResposta;
import com.burguer.restaurant.dto.pedido.PedidoStatusRequisicao;

public interface PedidoService {

    List<PedidoResposta> listarTodos(StatusPedido status);

    PedidoResposta criarCheckout(PedidoCheckoutRequisicao requisicao);

    PedidoResposta buscarPorId(Long id);

    PedidoResposta atualizarStatus(Long id, PedidoStatusRequisicao requisicao);

    void remover(Long id);
}
