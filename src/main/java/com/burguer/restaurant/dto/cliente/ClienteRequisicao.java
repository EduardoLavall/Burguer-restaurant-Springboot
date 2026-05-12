package com.burguer.restaurant.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequisicao(
        @NotBlank String nome,
        @Email @NotBlank String email,
        @NotBlank String enderecoEntrega,
        @NotBlank String preferenciaPagamento) {
}
