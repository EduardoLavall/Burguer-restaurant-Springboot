package com.burguer.restaurant.dto.cliente;

public record ClienteResposta(
        Long id,
        String nome,
        String email,
        String enderecoEntrega,
        String preferenciaPagamento) {
}
