package com.burguer.restaurant.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.burguer.restaurant.dominio.pedido.StatusPedido;
import com.burguer.restaurant.dto.pedido.PedidoResposta;
import com.burguer.restaurant.dto.pedido.PedidoStatusRequisicao;
import com.burguer.restaurant.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/pedidos")
public class AdminPedidoController {

    private final PedidoService pedidoService;

    public AdminPedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResposta>> listarTodos(@RequestParam(required = false) StatusPedido status) {
        return ResponseEntity.ok(pedidoService.listarTodos(status));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResposta> atualizarStatus(@PathVariable Long id,
            @Valid @RequestBody PedidoStatusRequisicao requisicao) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, requisicao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        pedidoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
