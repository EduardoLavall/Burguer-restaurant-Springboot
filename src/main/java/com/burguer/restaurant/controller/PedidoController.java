package com.burguer.restaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.burguer.restaurant.dto.PedidoDto;
import com.burguer.restaurant.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/{id}")
    public PedidoDto.Resposta buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoDto.Resposta criarCheckout(@Valid @RequestBody PedidoDto.CheckoutRequisicao requisicao) {
        return pedidoService.criarCheckout(requisicao);
    }
}
