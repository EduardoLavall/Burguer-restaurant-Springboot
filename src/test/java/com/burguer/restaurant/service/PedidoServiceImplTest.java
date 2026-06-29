package com.burguer.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.burguer.restaurant.dominio.pedido.ItemPedido;
import com.burguer.restaurant.dominio.pedido.Pedido;
import com.burguer.restaurant.dominio.pedido.StatusPedido;
import com.burguer.restaurant.dominio.produto.CategoriaProduto;
import com.burguer.restaurant.dominio.produto.Produto;
import com.burguer.restaurant.dto.pedido.ItemPedidoRequisicao;
import com.burguer.restaurant.dto.pedido.PedidoCheckoutRequisicao;
import com.burguer.restaurant.dto.pedido.PedidoStatusRequisicao;
import com.burguer.restaurant.exception.RegraNegocioException;
import com.burguer.restaurant.repository.PedidoRepository;
import com.burguer.restaurant.repository.ProdutoRepository;
import com.burguer.restaurant.service.impl.PedidoServiceImpl;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Test
    void deveCriarCheckoutComTotalCalculadoNoBackend() {
        Produto burger = new Produto(
                1L,
                "Burger Bacon",
                "Pao brioche e bacon",
                new BigDecimal("30.00"),
                CategoriaProduto.comida,
                true,
                null);

        Produto refrigerante = new Produto(
                2L,
                "Refrigerante",
                "Lata 350ml",
                new BigDecimal("8.00"),
                CategoriaProduto.bebida,
                true,
                null);

        when(produtoRepository.buscarPorId(1L)).thenReturn(Optional.of(burger));
        when(produtoRepository.buscarPorId(2L)).thenReturn(Optional.of(refrigerante));
        when(pedidoRepository.salvar(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            return new Pedido(
                    99L,
                    pedido.getNomeCliente(),
                    pedido.getNumeroMesa(),
                    pedido.getItensPedido(),
                    pedido.getStatus(),
                    pedido.getDataCriacao(),
                    pedido.getDataAtualizacao());
        });

        PedidoService pedidoService = new PedidoServiceImpl(pedidoRepository, produtoRepository);

        var resposta = pedidoService.criarCheckout(new PedidoCheckoutRequisicao(
                "Marina",
                12,
                List.of(
                        new ItemPedidoRequisicao(1L, 2),
                        new ItemPedidoRequisicao(2L, 1))));

        assertThat(resposta.id()).isEqualTo(99L);
        assertThat(resposta.nomeCliente()).isEqualTo("Marina");
        assertThat(resposta.numeroMesa()).isEqualTo(12);
        assertThat(resposta.status()).isEqualTo(StatusPedido.recebido);
        assertThat(resposta.subtotal()).isEqualByComparingTo("68.00");
        assertThat(resposta.taxaServico()).isEqualByComparingTo("6.80");
        assertThat(resposta.valorTotal()).isEqualByComparingTo("74.80");
        assertThat(resposta.itens()).hasSize(2);
    }

    @Test
    void deveImpedirCheckoutComProdutoInativo() {
        Produto produtoInativo = new Produto(
                1L,
                "Burger fora do menu",
                "Indisponivel",
                new BigDecimal("25.00"),
                CategoriaProduto.comida,
                false,
                null);

        when(produtoRepository.buscarPorId(1L)).thenReturn(Optional.of(produtoInativo));

        PedidoService pedidoService = new PedidoServiceImpl(pedidoRepository, produtoRepository);

        assertThatThrownBy(() -> pedidoService.criarCheckout(new PedidoCheckoutRequisicao(
                "Lucas",
                4,
                List.of(new ItemPedidoRequisicao(1L, 1)))))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Produto indisponivel");
    }

    @Test
    void deveAtualizarStatusOperacionalDoPedido() {
        Produto produto = new Produto(
                3L,
                "Batata",
                "Porcao media",
                new BigDecimal("14.00"),
                CategoriaProduto.acompanhamento,
                true,
                null);

        Pedido pedido = new Pedido(
                20L,
                "Paulo",
                8,
                List.of(new ItemPedido(produto, 1)),
                StatusPedido.recebido,
                OffsetDateTime.parse("2026-06-16T20:00:00Z"),
                OffsetDateTime.parse("2026-06-16T20:00:00Z"));

        when(pedidoRepository.buscarPorId(20L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.salvar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoService pedidoService = new PedidoServiceImpl(pedidoRepository, produtoRepository);

        var resposta = pedidoService.atualizarStatus(20L, new PedidoStatusRequisicao(StatusPedido.em_preparo));

        assertThat(resposta.status()).isEqualTo(StatusPedido.em_preparo);
        assertThat(resposta.dataAtualizacao()).isAfter(pedido.getDataAtualizacao());
        assertThat(resposta.valorTotal()).isEqualByComparingTo("15.40");
    }

    @Test
    void devePermitirExcluirPedidoCancelado() {
        Produto produto = new Produto(
                4L,
                "Milkshake",
                "Chocolate",
                new BigDecimal("18.00"),
                CategoriaProduto.bebida,
                true,
                null);

        Pedido pedido = new Pedido(
                30L,
                "Fernanda",
                2,
                List.of(new ItemPedido(produto, 1)),
                StatusPedido.cancelado,
                OffsetDateTime.parse("2026-06-16T20:00:00Z"),
                OffsetDateTime.parse("2026-06-16T20:05:00Z"));

        when(pedidoRepository.buscarPorId(30L)).thenReturn(Optional.of(pedido));

        PedidoService pedidoService = new PedidoServiceImpl(pedidoRepository, produtoRepository);

        pedidoService.remover(30L);

        verify(pedidoRepository).removerPorId(30L);
    }

    @Test
    void deveImpedirExcluirPedidoEmAndamento() {
        Produto produto = new Produto(
                5L,
                "Burger duplo",
                "Cheddar e cebola",
                new BigDecimal("32.00"),
                CategoriaProduto.comida,
                true,
                null);

        Pedido pedido = new Pedido(
                31L,
                "Rafael",
                9,
                List.of(new ItemPedido(produto, 1)),
                StatusPedido.em_preparo,
                OffsetDateTime.parse("2026-06-16T20:00:00Z"),
                OffsetDateTime.parse("2026-06-16T20:07:00Z"));

        when(pedidoRepository.buscarPorId(31L)).thenReturn(Optional.of(pedido));

        PedidoService pedidoService = new PedidoServiceImpl(pedidoRepository, produtoRepository);

        assertThatThrownBy(() -> pedidoService.remover(31L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("So e permitido excluir pedidos cancelados ou entregues.");

        verify(pedidoRepository, never()).removerPorId(31L);
        verifyNoInteractions(produtoRepository);
    }
}
