package com.burguer.restaurant.repository.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.burguer.restaurant.dominio.pedido.ItemPedido;
import com.burguer.restaurant.dominio.pedido.Pedido;
import com.burguer.restaurant.dominio.pedido.StatusPedido;
import com.burguer.restaurant.dominio.produto.CategoriaProduto;
import com.burguer.restaurant.dominio.produto.Produto;
import com.burguer.restaurant.repository.PedidoRepository;
import com.burguer.restaurant.repository.ProdutoRepository;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:./target/test-restaurant.db"
})
class PedidoRepositoryJdbcTest {

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
        Produto burger = produtoRepository.salvar(new Produto(
                null,
                "Burger Smash",
                "Pao, carne e queijo",
                new BigDecimal("29.90"),
                CategoriaProduto.comida,
                true,
                null));

        Produto fritas = produtoRepository.salvar(new Produto(
                null,
                "Batata rustica",
                "Porcao grande",
                new BigDecimal("15.50"),
                CategoriaProduto.acompanhamento,
                true,
                null));

        OffsetDateTime agora = OffsetDateTime.now();
        Pedido pedidoSalvo = pedidoRepository.salvar(new Pedido(
                null,
                "Juliana",
                5,
                List.of(
                        new ItemPedido(burger, 2),
                        new ItemPedido(fritas, 1)),
                StatusPedido.recebido,
                agora,
                agora));

        var pedidoEncontrado = pedidoRepository.buscarPorId(pedidoSalvo.getId()).orElseThrow();

        assertThat(pedidoEncontrado.getNomeCliente()).isEqualTo("Juliana");
        assertThat(pedidoEncontrado.getNumeroMesa()).isEqualTo(5);
        assertThat(pedidoEncontrado.getStatus()).isEqualTo(StatusPedido.recebido);
        assertThat(pedidoEncontrado.getItensPedido()).hasSize(2);
        assertThat(pedidoEncontrado.getSubtotal()).isEqualByComparingTo("75.30");
        assertThat(pedidoEncontrado.getTaxaServico()).isEqualByComparingTo("7.53");
        assertThat(pedidoEncontrado.getValorTotal()).isEqualByComparingTo("82.83");
    }
}
