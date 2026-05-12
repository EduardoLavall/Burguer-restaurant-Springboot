package com.burguer.restaurant.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burguer.restaurant.dto.cliente.ClienteRequisicao;
import com.burguer.restaurant.dto.cliente.ClienteResposta;
import com.burguer.restaurant.servico.ClienteServico;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteControlador {

    private final ClienteServico clienteServico;

    public ClienteControlador(ClienteServico clienteServico) {
        this.clienteServico = clienteServico;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResposta>> listarTodos() {
        return ResponseEntity.ok(clienteServico.listarTodos());
    }

    @PostMapping
    public ResponseEntity<ClienteResposta> criar(@Valid @RequestBody ClienteRequisicao requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteServico.criar(requisicao));
    }
}
