package com.burguer.restaurant.dto.pedido;

import java.math.BigDecimal;

public record ItemPedidoResposta(
        Long produtoId,
        String nomeProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal) {
}
