package com.burguer.restaurant.dto.pedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemPedidoRequisicao(
        @NotNull Long produtoId,
        @NotNull @Min(1) Integer quantidade) {
}
