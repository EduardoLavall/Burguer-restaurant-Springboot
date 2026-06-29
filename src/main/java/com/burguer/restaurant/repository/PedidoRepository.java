package com.burguer.restaurant.repository;

import java.util.List;
import java.util.Optional;

import com.burguer.restaurant.dominio.pedido.Pedido;
import com.burguer.restaurant.dominio.pedido.StatusPedido;

public interface PedidoRepository {

    List<Pedido> listarTodos();

    List<Pedido> listarPorStatus(StatusPedido status);

    Optional<Pedido> buscarPorId(Long id);

    Pedido salvar(Pedido pedido);

    void removerPorId(Long id);
}
