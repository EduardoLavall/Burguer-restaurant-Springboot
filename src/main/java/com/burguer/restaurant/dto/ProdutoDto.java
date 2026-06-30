package com.burguer.restaurant.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class ProdutoDto {

    private ProdutoDto() {
    }

    public enum Categoria {
        comida,
        bebida,
        acompanhamento,
        doce
    }

    public record Requisicao(
            @NotBlank String nome,
            @NotBlank String descricao,
            @NotNull @DecimalMin("0.0") BigDecimal preco,
            @NotNull Categoria categoria,
            @NotNull Boolean disponibilidade,
            String imagem) {
    }

    public record Resposta(
            Long id,
            String nome,
            String descricao,
            BigDecimal preco,
            Categoria categoria,
            boolean disponibilidade,
            String imagem) {
    }

    public record AtualizacaoPreco(
            @NotNull @DecimalMin("0.0") BigDecimal preco) {
    }

    public record AtualizacaoStatus(
            @NotNull Boolean disponibilidade) {
    }

    public record CardapioResposta(
            Long id,
            String nome,
            String descricao,
            BigDecimal preco,
            Categoria categoria,
            String imagem) {
    }
}
