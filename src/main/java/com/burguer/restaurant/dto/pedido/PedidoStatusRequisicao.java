package com.burguer.restaurant.dto.pedido;

import com.burguer.restaurant.dominio.pedido.StatusPedido;

import jakarta.validation.constraints.NotNull;

public record PedidoStatusRequisicao(
        @NotNull StatusPedido status) {
}
