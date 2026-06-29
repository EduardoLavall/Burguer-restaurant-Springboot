package com.burguer.restaurant.dominio.pedido;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

public class Pedido {

    private static final BigDecimal TAXA_SERVICO = new BigDecimal("0.10");

    private final Long id;
    private final String nomeCliente;
    private final Integer numeroMesa;
    private final List<ItemPedido> itensPedido;
    private final StatusPedido status;
    private final OffsetDateTime dataCriacao;
    private final OffsetDateTime dataAtualizacao;

    public Pedido(Long id, String nomeCliente, Integer numeroMesa, List<ItemPedido> itensPedido, StatusPedido status,
            OffsetDateTime dataCriacao, OffsetDateTime dataAtualizacao) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.numeroMesa = numeroMesa;
        this.itensPedido = List.copyOf(itensPedido);
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public Long getId() {
        return id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public Integer getNumeroMesa() {
        return numeroMesa;
    }

    public List<ItemPedido> getItensPedido() {
        return itensPedido;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public OffsetDateTime getDataCriacao() {
        return dataCriacao;
    }

    public OffsetDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    // O subtotal sempre vem da soma dos itens para evitar valor divergente salvo de fora.
    public BigDecimal getSubtotal() {
        return itensPedido.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // A v1 do trabalho usa taxa fixa de 10 por cento para todos os pedidos.
    public BigDecimal getTaxaServico() {
        return getSubtotal()
                .multiply(TAXA_SERVICO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getValorTotal() {
        return getSubtotal()
                .add(getTaxaServico())
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Ao mudar o status, mantemos o restante do pedido igual e atualizamos so a data da operacao.
    public Pedido comStatus(StatusPedido novoStatus, OffsetDateTime novaDataAtualizacao) {
        return new Pedido(
                id,
                nomeCliente,
                numeroMesa,
                itensPedido,
                novoStatus,
                dataCriacao,
                novaDataAtualizacao);
    }
}
