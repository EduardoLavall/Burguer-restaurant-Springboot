package com.burguer.restaurant.dto.produto;

import java.math.BigDecimal;

import com.burguer.restaurant.dominio.produto.CategoriaProduto;

public record ProdutoResposta(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        CategoriaProduto categoria,
        boolean disponibilidade,
        String imagem) {
}
