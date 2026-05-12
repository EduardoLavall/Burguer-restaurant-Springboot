package com.burguer.restaurant.dto.produto;

import java.math.BigDecimal;

import com.burguer.restaurant.dominio.produto.CategoriaProduto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProdutoRequisicao(
        @NotBlank String nome,
        @NotBlank String descricao,
        @NotNull @DecimalMin("0.0") BigDecimal preco,
        @NotNull CategoriaProduto categoria,
        @NotNull Boolean disponibilidade,
        String imagem) {
}
