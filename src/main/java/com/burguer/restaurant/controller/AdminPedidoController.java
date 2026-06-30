package com.burguer.restaurant.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.burguer.restaurant.dto.PedidoDto;
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
    public List<PedidoDto.Resposta> listarTodos(@RequestParam(required = false) PedidoDto.Status status) {
        return pedidoService.listarTodos(status);
    }

    @PatchMapping("/{id}/status")
    public PedidoDto.Resposta atualizarStatus(@PathVariable Long id,
            @Valid @RequestBody PedidoDto.AtualizacaoStatusRequisicao requisicao) {
        return pedidoService.atualizarStatus(id, requisicao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        pedidoService.remover(id);
    }
}
