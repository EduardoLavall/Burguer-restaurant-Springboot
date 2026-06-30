package com.burguer.restaurant.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:./target/test-restaurant.db"
})
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limparBase() {
        jdbcTemplate.update("DELETE FROM item_pedido");
        jdbcTemplate.update("DELETE FROM pedido");
        jdbcTemplate.update("DELETE FROM produto");
    }

    @Test
    void devePersistirEPesquisarPedidoComItens() {
        ProdutoRepository.Produto burger = produtoRepository.salvar(new ProdutoRepository.Produto(
                null,
                "Burger Smash",
                "Pao, carne e queijo",
                new BigDecimal("29.90"),
                ProdutoRepository.Produto.Categoria.comida,
                true,
                null));

        ProdutoRepository.Produto fritas = produtoRepository.salvar(new ProdutoRepository.Produto(
                null,
                "Batata rustica",
                "Porcao grande",
                new BigDecimal("15.50"),
                ProdutoRepository.Produto.Categoria.acompanhamento,
                true,
                null));

        OffsetDateTime agora = OffsetDateTime.now();
        PedidoRepository.Pedido pedidoSalvo = pedidoRepository.salvar(new PedidoRepository.Pedido(
                null,
                "Juliana",
                5,
                List.of(
                        new PedidoRepository.ItemPedido(burger, 2),
                        new PedidoRepository.ItemPedido(fritas, 1)),
                PedidoRepository.Pedido.Status.recebido,
                agora,
                agora));

        var pedidoEncontrado = pedidoRepository.buscarPorId(pedidoSalvo.getId()).orElseThrow();

        assertThat(pedidoEncontrado.getNomeCliente()).isEqualTo("Juliana");
        assertThat(pedidoEncontrado.getNumeroMesa()).isEqualTo(5);
        assertThat(pedidoEncontrado.getStatus()).isEqualTo(PedidoRepository.Pedido.Status.recebido);
        assertThat(pedidoEncontrado.getItensPedido()).hasSize(2);
        assertThat(pedidoEncontrado.getSubtotal()).isEqualByComparingTo("75.30");
        assertThat(pedidoEncontrado.getTaxaServico()).isEqualByComparingTo("7.53");
        assertThat(pedidoEncontrado.getValorTotal()).isEqualByComparingTo("82.83");
    }
}
