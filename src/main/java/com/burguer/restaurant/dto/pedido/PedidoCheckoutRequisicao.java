package com.burguer.restaurant.dto.pedido;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PedidoCheckoutRequisicao(
        @NotBlank String nomeCliente,
        @NotNull @Min(1) Integer numeroMesa,
        @Valid @NotEmpty List<ItemPedidoRequisicao> itens) {
}
