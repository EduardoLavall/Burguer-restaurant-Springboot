package com.burguer.restaurant.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PedidoRepository {

    private static final String SQL_LISTAR_PEDIDOS =
            "SELECT id, nome_cliente, numero_mesa, status, data_criacao, data_atualizacao FROM pedido "
                    + "ORDER BY CASE WHEN status IN ('entregue', 'cancelado') THEN 1 ELSE 0 END, data_criacao DESC";
    private static final String SQL_LISTAR_PEDIDOS_POR_STATUS =
            "SELECT id, nome_cliente, numero_mesa, status, data_criacao, data_atualizacao FROM pedido "
                    + "WHERE status = ? ORDER BY data_criacao DESC";
    private static final String SQL_BUSCAR_PEDIDO_POR_ID =
            "SELECT id, nome_cliente, numero_mesa, status, data_criacao, data_atualizacao FROM pedido WHERE id = ?";
    private static final String SQL_LISTAR_ITENS_DO_PEDIDO =
            "SELECT produto_id, quantidade FROM item_pedido WHERE pedido_id = ?";
    private static final String SQL_INSERIR_PEDIDO =
            "INSERT INTO pedido (nome_cliente, numero_mesa, subtotal, taxa_servico, valor_total, status, data_criacao, data_atualizacao) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_ATUALIZAR_PEDIDO =
            "UPDATE pedido SET nome_cliente = ?, numero_mesa = ?, subtotal = ?, taxa_servico = ?, valor_total = ?, "
                    + "status = ?, data_criacao = ?, data_atualizacao = ? WHERE id = ?";
    private static final String SQL_INSERIR_ITEM_PEDIDO =
            "INSERT INTO item_pedido (pedido_id, produto_id, quantidade) VALUES (?, ?, ?)";
    private static final String SQL_REMOVER_ITENS_DO_PEDIDO =
            "DELETE FROM item_pedido WHERE pedido_id = ?";
    private static final String SQL_REMOVER_PEDIDO =
            "DELETE FROM pedido WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final ProdutoRepository produtoRepository;

    public PedidoRepository(JdbcTemplate jdbcTemplate, ProdutoRepository produtoRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.produtoRepository = produtoRepository;
    }

    public List<Pedido> listarTodos() {
        return listarPedidos(SQL_LISTAR_PEDIDOS);
    }

    public List<Pedido> listarPorStatus(Pedido.Status status) {
        return listarPedidos(SQL_LISTAR_PEDIDOS_POR_STATUS, status.name());
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return listarPedidos(SQL_BUSCAR_PEDIDO_POR_ID, id).stream().findFirst();
    }

    public Pedido salvar(Pedido pedido) {
        if (pedido.getId() == null) {
            return inserirPedido(pedido);
        }

        jdbcTemplate.update(SQL_ATUALIZAR_PEDIDO,
                pedido.getNomeCliente(),
                pedido.getNumeroMesa(),
                pedido.getSubtotal(),
                pedido.getTaxaServico(),
                pedido.getValorTotal(),
                pedido.getStatus().name(),
                pedido.getDataCriacao().toString(),
                pedido.getDataAtualizacao().toString(),
                pedido.getId());

        // Eu apago e recrio os itens para o banco ficar igual ao estado atual do pedido.
        jdbcTemplate.update(SQL_REMOVER_ITENS_DO_PEDIDO, pedido.getId());
        salvarItensPedido(pedido.getId(), pedido.getItensPedido());

        return pedido;
    }

    public void removerPorId(Long id) {
        jdbcTemplate.update(SQL_REMOVER_ITENS_DO_PEDIDO, id);
        jdbcTemplate.update(SQL_REMOVER_PEDIDO, id);
    }

    private List<Pedido> listarPedidos(String sql, Object... parametros) {
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapearPedidoCompleto(rs), parametros);
    }

    private Pedido inserirPedido(Pedido pedido) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERIR_PEDIDO, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pedido.getNomeCliente());
            ps.setInt(2, pedido.getNumeroMesa());
            ps.setBigDecimal(3, pedido.getSubtotal());
            ps.setBigDecimal(4, pedido.getTaxaServico());
            ps.setBigDecimal(5, pedido.getValorTotal());
            ps.setString(6, pedido.getStatus().name());
            ps.setString(7, pedido.getDataCriacao().toString());
            ps.setString(8, pedido.getDataAtualizacao().toString());
            return ps;
        }, keyHolder);

        // Primeiro salvo o cabecalho do pedido e depois uso o id gerado para gravar os itens.
        Long id = keyHolder.getKey().longValue();
        salvarItensPedido(id, pedido.getItensPedido());

        return new Pedido(id, pedido.getNomeCliente(), pedido.getNumeroMesa(), pedido.getItensPedido(),
                pedido.getStatus(), pedido.getDataCriacao(), pedido.getDataAtualizacao());
    }

    private void salvarItensPedido(Long pedidoId, List<ItemPedido> itensPedido) {
        for (ItemPedido item : itensPedido) {
            jdbcTemplate.update(SQL_INSERIR_ITEM_PEDIDO, pedidoId, item.getProduto().getId(), item.getQuantidade());
        }
    }

    private Pedido mapearPedidoCompleto(ResultSet rs) throws SQLException {
        Long pedidoId = rs.getLong("id");
        List<ItemPedido> itensPedido = buscarItensPedido(pedidoId);

        return new Pedido(
                pedidoId,
                rs.getString("nome_cliente"),
                rs.getInt("numero_mesa"),
                itensPedido,
                Pedido.Status.valueOf(rs.getString("status")),
                OffsetDateTime.parse(rs.getString("data_criacao")),
                OffsetDateTime.parse(rs.getString("data_atualizacao")));
    }

    private List<ItemPedido> buscarItensPedido(Long pedidoId) {
        return jdbcTemplate.query(SQL_LISTAR_ITENS_DO_PEDIDO, (itemRs, rowNum) -> {
            Long produtoId = itemRs.getLong("produto_id");
            int quantidade = itemRs.getInt("quantidade");
            // O item guarda so o id do produto, entao aqui eu remonto o objeto completo para o modelo interno.
            Produto produto = produtoRepository.buscarPorId(produtoId).orElseThrow();
            return new ItemPedido(produto, quantidade);
        }, pedidoId);
    }
}
