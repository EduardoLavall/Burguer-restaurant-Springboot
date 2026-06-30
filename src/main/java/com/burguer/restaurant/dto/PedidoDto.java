package com.burguer.restaurant.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public final class PedidoDto {

    private PedidoDto() {
    }

    public enum Status {
        recebido,
        em_preparo,
        pronto,
        entregue,
        cancelado
    }

    public record ItemRequisicao(
            @NotNull Long produtoId,
            @NotNull @Min(1) Integer quantidade) {
    }

    public record ItemResposta(
            Long produtoId,
            String nomeProduto,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal subtotal) {
    }

    public record CheckoutRequisicao(
            @NotBlank String nomeCliente,
            @NotNull @Min(1) Integer numeroMesa,
            @Valid @NotEmpty List<ItemRequisicao> itens) {
    }

    public record AtualizacaoStatusRequisicao(
            @NotNull Status status) {
    }

    public record Resposta(
            Long id,
            String nomeCliente,
            Integer numeroMesa,
            List<ItemResposta> itens,
            BigDecimal subtotal,
            BigDecimal taxaServico,
            BigDecimal valorTotal,
            Status status,
            OffsetDateTime dataCriacao,
            OffsetDateTime dataAtualizacao) {
    }
}
