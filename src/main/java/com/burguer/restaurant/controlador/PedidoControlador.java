package com.burguer.restaurant.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burguer.restaurant.dto.pedido.PedidoRequisicao;
import com.burguer.restaurant.dto.pedido.PedidoResposta;
import com.burguer.restaurant.servico.PedidoServico;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoControlador {

    private final PedidoServico pedidoServico;

    public PedidoControlador(PedidoServico pedidoServico) {
        this.pedidoServico = pedidoServico;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResposta>> listarTodos() {
        return ResponseEntity.ok(pedidoServico.listarTodos());
    }

    @PostMapping
    public ResponseEntity<PedidoResposta> criar(@Valid @RequestBody PedidoRequisicao requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoServico.criar(requisicao));
    }
}
