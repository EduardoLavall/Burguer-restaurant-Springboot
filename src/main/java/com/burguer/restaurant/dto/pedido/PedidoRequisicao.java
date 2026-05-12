package com.burguer.restaurant.dto.pedido;

import java.util.List;

import com.burguer.restaurant.dominio.pedido.StatusPedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PedidoRequisicao(
        @NotNull Long clienteId,
        @Valid @NotEmpty List<ItemPedidoRequisicao> itensPedido,
        @NotNull StatusPedido status) {
}
