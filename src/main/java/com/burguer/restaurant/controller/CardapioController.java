package com.burguer.restaurant.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burguer.restaurant.dto.ProdutoDto;
import com.burguer.restaurant.service.ProdutoService;

@RestController
@RequestMapping("/api/cardapio")
public class CardapioController {

    private final ProdutoService produtoService;

    public CardapioController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<ProdutoDto.CardapioResposta> listarAtivos() {
        return produtoService.listarCardapio();
    }
}
