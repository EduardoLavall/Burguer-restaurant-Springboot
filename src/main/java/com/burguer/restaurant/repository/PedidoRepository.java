package com.burguer.restaurant.repository;

import java.util.List;

import com.burguer.restaurant.dominio.pedido.Pedido;

public interface PedidoRepository {

    List<Pedido> listarTodos();

    Pedido salvar(Pedido pedido);
}
