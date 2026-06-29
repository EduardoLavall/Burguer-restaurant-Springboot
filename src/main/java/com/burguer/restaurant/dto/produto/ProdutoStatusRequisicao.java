package com.burguer.restaurant.dto.produto;

import jakarta.validation.constraints.NotNull;

public record ProdutoStatusRequisicao(
        @NotNull Boolean disponibilidade) {
}
