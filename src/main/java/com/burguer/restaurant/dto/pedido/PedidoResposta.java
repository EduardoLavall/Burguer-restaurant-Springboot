package com.burguer.restaurant.dto.pedido;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.burguer.restaurant.dominio.pedido.StatusPedido;

public record PedidoResposta(
        Long id,
        Long clienteId,
        List<ItemPedidoResposta> itensPedido,
        BigDecimal subtotal,
        BigDecimal taxaServico,
        BigDecimal valorTotal,
        StatusPedido status,
        OffsetDateTime dataPedido,
        OffsetDateTime dataEntrega) {
}
