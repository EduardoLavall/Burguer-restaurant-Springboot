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

import com.burguer.restaurant.dto.PedidoDto;
import com.burguer.restaurant.repository.ItemPedido;
import com.burguer.restaurant.repository.Pedido;
import com.burguer.restaurant.repository.PedidoRepository;
import com.burguer.restaurant.repository.Produto;
import com.burguer.restaurant.repository.ProdutoRepository;

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
                Produto.Categoria.comida,
                true,
                null);

        Produto refrigerante = new Produto(
                2L,
                "Refrigerante",
                "Lata 350ml",
                new BigDecimal("8.00"),
                Produto.Categoria.bebida,
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

        PedidoService pedidoService = new PedidoService(pedidoRepository, produtoRepository);

        var resposta = pedidoService.criarCheckout(new PedidoDto.CheckoutRequisicao(
                "Marina",
                12,
                List.of(
                        new PedidoDto.ItemRequisicao(1L, 2),
                        new PedidoDto.ItemRequisicao(2L, 1))));

        assertThat(resposta.id()).isEqualTo(99L);
        assertThat(resposta.nomeCliente()).isEqualTo("Marina");
        assertThat(resposta.numeroMesa()).isEqualTo(12);
        assertThat(resposta.status()).isEqualTo(PedidoDto.Status.recebido);
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
                Produto.Categoria.comida,
                false,
                null);

        when(produtoRepository.buscarPorId(1L)).thenReturn(Optional.of(produtoInativo));

        PedidoService pedidoService = new PedidoService(pedidoRepository, produtoRepository);

        assertThatThrownBy(() -> pedidoService.criarCheckout(new PedidoDto.CheckoutRequisicao(
                "Lucas",
                4,
                List.of(new PedidoDto.ItemRequisicao(1L, 1)))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Produto indisponivel");
    }

    @Test
    void deveAtualizarStatusOperacionalDoPedido() {
        Produto produto = new Produto(
                3L,
                "Batata",
                "Porcao media",
                new BigDecimal("14.00"),
                Produto.Categoria.acompanhamento,
                true,
                null);

        Pedido pedido = new Pedido(
                20L,
                "Paulo",
                8,
                List.of(new ItemPedido(produto, 1)),
                Pedido.Status.recebido,
                OffsetDateTime.parse("2026-06-16T20:00:00Z"),
                OffsetDateTime.parse("2026-06-16T20:00:00Z"));

        when(pedidoRepository.buscarPorId(20L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.salvar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoService pedidoService = new PedidoService(pedidoRepository, produtoRepository);

        var resposta = pedidoService.atualizarStatus(20L, new PedidoDto.AtualizacaoStatusRequisicao(PedidoDto.Status.em_preparo));

        assertThat(resposta.status()).isEqualTo(PedidoDto.Status.em_preparo);
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
                Produto.Categoria.bebida,
                true,
                null);

        Pedido pedido = new Pedido(
                30L,
                "Fernanda",
                2,
                List.of(new ItemPedido(produto, 1)),
                Pedido.Status.cancelado,
                OffsetDateTime.parse("2026-06-16T20:00:00Z"),
                OffsetDateTime.parse("2026-06-16T20:05:00Z"));

        when(pedidoRepository.buscarPorId(30L)).thenReturn(Optional.of(pedido));

        PedidoService pedidoService = new PedidoService(pedidoRepository, produtoRepository);

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
                Produto.Categoria.comida,
                true,
                null);

        Pedido pedido = new Pedido(
                31L,
                "Rafael",
                9,
                List.of(new ItemPedido(produto, 1)),
                Pedido.Status.em_preparo,
                OffsetDateTime.parse("2026-06-16T20:00:00Z"),
                OffsetDateTime.parse("2026-06-16T20:07:00Z"));

        when(pedidoRepository.buscarPorId(31L)).thenReturn(Optional.of(pedido));

        PedidoService pedidoService = new PedidoService(pedidoRepository, produtoRepository);

        assertThatThrownBy(() -> pedidoService.remover(31L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("So e permitido excluir pedidos cancelados ou entregues.");

        verify(pedidoRepository, never()).removerPorId(31L);
        verifyNoInteractions(produtoRepository);
    }
}
