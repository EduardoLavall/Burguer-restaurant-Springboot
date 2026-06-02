package com.burguer.restaurant.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import com.burguer.restaurant.dominio.pedido.ItemPedido;
import com.burguer.restaurant.dominio.pedido.Pedido;
import com.burguer.restaurant.dominio.produto.Produto;
import com.burguer.restaurant.repository.PedidoRepository;
import com.burguer.restaurant.repository.ProdutoRepository;

@Repository
@Profile("sqlite")
public class PedidoRepositoryJdbc implements PedidoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ProdutoRepository produtoRepository;

    public PedidoRepositoryJdbc(JdbcTemplate jdbcTemplate, ProdutoRepository produtoRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.produtoRepository = produtoRepository;
    }

    @Override
    public List<Pedido> listarTodos() {
        List<Pedido> pedidos = new ArrayList<>();
        jdbcTemplate.query("SELECT id, cliente_id, valor_total, status, data_pedido, data_entrega FROM pedido",
                rs -> {
                    Long id = rs.getLong("id");
                    Long clienteId = rs.getLong("cliente_id");
                    String status = rs.getString("status");
                    OffsetDateTime dataPedido = OffsetDateTime.parse(rs.getString("data_pedido"));
                    String dataEntregaStr = rs.getString("data_entrega");
                    OffsetDateTime dataEntrega = dataEntregaStr == null ? null : OffsetDateTime.parse(dataEntregaStr);

                    // carregar itens
                    List<ItemPedido> itens = jdbcTemplate.query("SELECT produto_id, quantidade FROM item_pedido WHERE pedido_id = ?",
                            (r, rowNum) -> {
                                Long produtoId = r.getLong("produto_id");
                                int quantidade = r.getInt("quantidade");
                                Produto produto = produtoRepository.buscarPorId(produtoId).orElseThrow();
                                return new ItemPedido(produto, quantidade);
                            }, id);

                    pedidos.add(new Pedido(id, clienteId, itens, com.burguer.restaurant.dominio.pedido.StatusPedido.valueOf(status), dataPedido, dataEntrega));
                });

        return pedidos;
    }

    @Override
    public Pedido salvar(Pedido pedido) {
        if (pedido.getId() == null) {
            String sql = "INSERT INTO pedido (cliente_id, valor_total, status, data_pedido, data_entrega) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, pedido.getClienteId());
                ps.setBigDecimal(2, pedido.getValorTotal());
                ps.setString(3, pedido.getStatus().name());
                ps.setString(4, pedido.getDataPedido().toString());
                ps.setString(5, pedido.getDataEntrega() == null ? null : pedido.getDataEntrega().toString());
                return ps;
            }, keyHolder);

            Long id = keyHolder.getKey().longValue();

            // inserir itens
            for (ItemPedido item : pedido.getItensPedido()) {
                jdbcTemplate.update("INSERT INTO item_pedido (pedido_id, produto_id, quantidade) VALUES (?, ?, ?)",
                        id, item.getProduto().getId(), item.getQuantidade());
            }

            return new Pedido(id, pedido.getClienteId(), pedido.getItensPedido(), pedido.getStatus(), pedido.getDataPedido(), pedido.getDataEntrega());
        } else {
            // atualizar pedido (valor/status/data_entrega)
            String sql = "UPDATE pedido SET cliente_id = ?, valor_total = ?, status = ?, data_pedido = ?, data_entrega = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    pedido.getClienteId(),
                    pedido.getValorTotal(),
                    pedido.getStatus().name(),
                    pedido.getDataPedido().toString(),
                    pedido.getDataEntrega() == null ? null : pedido.getDataEntrega().toString(),
                    pedido.getId());

            // simplificação: remover itens antigos e reinserir
            jdbcTemplate.update("DELETE FROM item_pedido WHERE pedido_id = ?", pedido.getId());
            for (ItemPedido item : pedido.getItensPedido()) {
                jdbcTemplate.update("INSERT INTO item_pedido (pedido_id, produto_id, quantidade) VALUES (?, ?, ?)",
                        pedido.getId(), item.getProduto().getId(), item.getQuantidade());
            }

            return pedido;
        }
    }
}
