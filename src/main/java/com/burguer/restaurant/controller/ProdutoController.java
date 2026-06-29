package com.burguer.restaurant.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burguer.restaurant.dto.produto.ProdutoRequisicao;
import com.burguer.restaurant.dto.produto.ProdutoResposta;
import com.burguer.restaurant.dto.produto.ProdutoStatusRequisicao;
import com.burguer.restaurant.service.ProdutoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResposta>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<ProdutoResposta> criar(@Valid @RequestBody ProdutoRequisicao requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criar(requisicao));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProdutoResposta> atualizar(@PathVariable Long id,
            @Valid @RequestBody ProdutoRequisicao requisicao) {
        return ResponseEntity.ok(produtoService.atualizar(id, requisicao));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProdutoResposta> atualizarStatus(@PathVariable Long id,
            @Valid @RequestBody ProdutoStatusRequisicao requisicao) {
        return ResponseEntity.ok(produtoService.atualizarStatus(id, requisicao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        produtoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
