package com.burguer.restaurant.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.burguer.restaurant.dto.ProdutoDto;
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
    public List<ProdutoDto.Resposta> listarTodos() {
        return produtoService.listarTodos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoDto.Resposta criar(@Valid @RequestBody ProdutoDto.Requisicao requisicao) {
        return produtoService.criar(requisicao);
    }

    @PatchMapping("/{id}")
    public ProdutoDto.Resposta atualizar(@PathVariable Long id,
            @Valid @RequestBody ProdutoDto.Requisicao requisicao) {
        return produtoService.atualizar(id, requisicao);
    }

    @PatchMapping("/{id}/status")
    public ProdutoDto.Resposta atualizarStatus(@PathVariable Long id,
            @Valid @RequestBody ProdutoDto.AtualizacaoStatus requisicao) {
        return produtoService.atualizarStatus(id, requisicao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        produtoService.remover(id);
    }
}
