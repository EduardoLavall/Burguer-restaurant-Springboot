package com.burguer.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.burguer.restaurant.dto.ProdutoDto;
import com.burguer.restaurant.repository.ProdutoRepository;
import com.burguer.restaurant.repository.ProdutoRepository.Produto;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceImplTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Test
    void deveListarSomenteProdutosAtivosNoCardapio() {
        Produto hamburguer = new Produto(
                1L,
                "Burger da Casa",
                "Pao, carne e cheddar",
                new BigDecimal("28.90"),
                Produto.Categoria.comida,
                true,
                null);

        Produto sobremesaIndisponivel = new Produto(
                2L,
                "Milk-shake",
                "Baunilha com calda",
                new BigDecimal("16.00"),
                Produto.Categoria.doce,
                false,
                null);

        when(produtoRepository.listarTodos()).thenReturn(List.of(hamburguer, sobremesaIndisponivel));

        ProdutoService produtoService = new ProdutoService(produtoRepository);

        var cardapio = produtoService.listarCardapio();

        assertThat(cardapio).hasSize(1);
        assertThat(cardapio.getFirst().nome()).isEqualTo("Burger da Casa");
        assertThat(cardapio.getFirst().categoria()).isEqualTo(ProdutoDto.Categoria.comida);
    }

    @Test
    void deveExcluirProdutoQuandoNaoHaPedidosRelacionados() {
        Produto produto = new Produto(
                3L,
                "Batata Rustica",
                "Porcao crocante",
                new BigDecimal("18.90"),
                Produto.Categoria.acompanhamento,
                true,
                null);

        when(produtoRepository.buscarPorId(3L)).thenReturn(Optional.of(produto));

        ProdutoService produtoService = new ProdutoService(produtoRepository);

        produtoService.remover(3L);

        verify(produtoRepository).removerPorId(3L);
    }

    @Test
    void deveImpedirExcluirProdutoQueJaEstaEmPedidos() {
        Produto produto = new Produto(
                4L,
                "Burger Especial",
                "Cheddar e cebola",
                new BigDecimal("31.90"),
                Produto.Categoria.comida,
                true,
                null);

        when(produtoRepository.buscarPorId(4L)).thenReturn(Optional.of(produto));
        doThrow(new DataIntegrityViolationException("fk_item_pedido_produto"))
                .when(produtoRepository)
                .removerPorId(4L);

        ProdutoService produtoService = new ProdutoService(produtoRepository);

        assertThatThrownBy(() -> produtoService.remover(4L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nao e permitido excluir produto que ja faz parte de pedidos.");
    }
}
