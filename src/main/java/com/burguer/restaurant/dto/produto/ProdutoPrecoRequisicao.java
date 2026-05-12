package com.burguer.restaurant.dto.produto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ProdutoPrecoRequisicao(
        @NotNull @DecimalMin("0.0") BigDecimal preco) {
}
