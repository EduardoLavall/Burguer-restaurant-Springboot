package com.burguer.restaurant.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.burguer.restaurant.dominio.produto.CategoriaProduto;
import com.burguer.restaurant.dominio.produto.Produto;
import com.burguer.restaurant.repository.ProdutoRepository;

@Repository
public class ProdutoRepositoryJdbc implements ProdutoRepository {

    private static final String SQL_LISTAR_PRODUTOS =
            "SELECT id, nome, descricao, preco, categoria, disponibilidade, imagem FROM produto";
    private static final String SQL_BUSCAR_PRODUTO_POR_ID =
            SQL_LISTAR_PRODUTOS + " WHERE id = ?";
    private static final String SQL_INSERIR_PRODUTO =
            "INSERT INTO produto (nome, descricao, preco, categoria, disponibilidade, imagem) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_ATUALIZAR_PRODUTO =
            "UPDATE produto SET nome = ?, descricao = ?, preco = ?, categoria = ?, disponibilidade = ?, imagem = ? WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    public ProdutoRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Produto> listarTodos() {
        return jdbcTemplate.query(SQL_LISTAR_PRODUTOS, (rs, rowNum) -> mapearProduto(rs));
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        var produtos = jdbcTemplate.query(SQL_BUSCAR_PRODUTO_POR_ID, (rs, rowNum) -> mapearProduto(rs), id);

        return produtos.stream().findFirst();
    }

    @Override
    public Produto salvar(Produto produto) {
        if (produto.getId() == null) {
            return inserirProduto(produto);
        } else {
            jdbcTemplate.update(SQL_ATUALIZAR_PRODUTO,
                    produto.getNome(),
                    produto.getDescricao(),
                    produto.getPreco(),
                    produto.getCategoria().name(),
                    produto.isDisponibilidade() ? 1 : 0,
                    produto.getImagem(),
                    produto.getId());
            return produto;
        }
    }

    @Override
    public void removerPorId(Long id) {
        jdbcTemplate.update("DELETE FROM produto WHERE id = ?", id);
    }

    private Produto inserirProduto(Produto produto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERIR_PRODUTO, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, produto.getNome());
            ps.setString(2, produto.getDescricao());
            ps.setBigDecimal(3, produto.getPreco());
            ps.setString(4, produto.getCategoria().name());
            ps.setInt(5, produto.isDisponibilidade() ? 1 : 0);
            ps.setString(6, produto.getImagem());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return new Produto(id, produto.getNome(), produto.getDescricao(), produto.getPreco(), produto.getCategoria(),
                produto.isDisponibilidade(), produto.getImagem());
    }

    private Produto mapearProduto(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("descricao"),
                rs.getBigDecimal("preco"),
                CategoriaProduto.valueOf(rs.getString("categoria")),
                rs.getInt("disponibilidade") != 0,
                rs.getString("imagem"));
    }
}
