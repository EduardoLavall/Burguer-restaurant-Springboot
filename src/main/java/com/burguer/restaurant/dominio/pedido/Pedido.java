package com.burguer.restaurant.dominio.pedido;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

public class Pedido {

    private static final BigDecimal TAXA_SERVICO = new BigDecimal("0.10");

    private final Long id;
    private final Long clienteId;
    private final List<ItemPedido> itensPedido;
    private final StatusPedido status;
    private final OffsetDateTime dataPedido;
    private final OffsetDateTime dataEntrega;

    public Pedido(Long id, Long clienteId, List<ItemPedido> itensPedido, StatusPedido status,
            OffsetDateTime dataPedido, OffsetDateTime dataEntrega) {
        this.id = id;
        this.clienteId = clienteId;
        this.itensPedido = List.copyOf(itensPedido);
        this.status = status;
        this.dataPedido = dataPedido;
        this.dataEntrega = dataEntrega;
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public List<ItemPedido> getItensPedido() {
        return itensPedido;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public OffsetDateTime getDataPedido() {
        return dataPedido;
    }

    public OffsetDateTime getDataEntrega() {
        return dataEntrega;
    }

    public BigDecimal getSubtotal() {
        return itensPedido.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTaxaServico() {
        // A taxa do atendimento fica centralizada no dominio para evitar calculos diferentes na API.
        return getSubtotal()
                .multiply(TAXA_SERVICO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getValorTotal() {
        return getSubtotal()
                .add(getTaxaServico())
                .setScale(2, RoundingMode.HALF_UP);
    }
}
