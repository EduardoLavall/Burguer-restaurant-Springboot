package com.burguer.restaurant.repositorio;

import java.util.List;

import com.burguer.restaurant.dominio.pedido.Pedido;

public interface PedidoRepositorio {

    List<Pedido> listarTodos();

    Pedido salvar(Pedido pedido);
}
