package com.burguer.restaurant.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burguer.restaurant.dto.produto.ProdutoRequisicao;
import com.burguer.restaurant.dto.produto.ProdutoResposta;
import com.burguer.restaurant.servico.ProdutoServico;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoControlador {

    private final ProdutoServico produtoServico;

    public ProdutoControlador(ProdutoServico produtoServico) {
        this.produtoServico = produtoServico;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResposta>> listarTodos() {
        return ResponseEntity.ok(produtoServico.listarTodos());
    }

    @PostMapping
    public ResponseEntity<ProdutoResposta> criar(@Valid @RequestBody ProdutoRequisicao requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoServico.criar(requisicao));
    }
}
