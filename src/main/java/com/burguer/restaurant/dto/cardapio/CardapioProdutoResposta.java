package com.burguer.restaurant.dto.cardapio;

import java.math.BigDecimal;

import com.burguer.restaurant.dominio.produto.CategoriaProduto;

public record CardapioProdutoResposta(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        CategoriaProduto categoria,
        String imagem) {
}
