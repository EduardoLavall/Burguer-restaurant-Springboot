package com.burguer.restaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burguer.restaurant.dto.pedido.PedidoCheckoutRequisicao;
import com.burguer.restaurant.dto.pedido.PedidoResposta;
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
    public ResponseEntity<PedidoResposta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @PostMapping("/checkout")
    public ResponseEntity<PedidoResposta> criarCheckout(@Valid @RequestBody PedidoCheckoutRequisicao requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criarCheckout(requisicao));
    }
}
