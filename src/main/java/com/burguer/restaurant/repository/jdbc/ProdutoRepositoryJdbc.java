package com.burguer.restaurant.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import com.burguer.restaurant.dominio.produto.CategoriaProduto;
import com.burguer.restaurant.dominio.produto.Produto;
import com.burguer.restaurant.repository.ProdutoRepository;

@Repository
@Profile("sqlite")
public class ProdutoRepositoryJdbc implements ProdutoRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProdutoRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Produto> listarTodos() {
        return jdbcTemplate.query("SELECT id, nome, descricao, preco, categoria, disponibilidade, imagem FROM produto",
                (rs, rowNum) -> new Produto(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getBigDecimal("preco"),
                        CategoriaProduto.valueOf(rs.getString("categoria")),
                        rs.getInt("disponibilidade") != 0,
                        rs.getString("imagem")
                ));
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        var list = jdbcTemplate.query("SELECT id, nome, descricao, preco, categoria, disponibilidade, imagem FROM produto WHERE id = ?",
                (rs, rowNum) -> new Produto(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getBigDecimal("preco"),
                        CategoriaProduto.valueOf(rs.getString("categoria")),
                        rs.getInt("disponibilidade") != 0,
                        rs.getString("imagem")
                ), id);

        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Produto salvar(Produto produto) {
        if (produto.getId() == null) {
            String sql = "INSERT INTO produto (nome, descricao, preco, categoria, disponibilidade, imagem) VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, produto.getNome());
                ps.setString(2, produto.getDescricao());
                ps.setBigDecimal(3, produto.getPreco());
                ps.setString(4, produto.getCategoria().name());
                ps.setInt(5, produto.isDisponibilidade() ? 1 : 0);
                ps.setString(6, produto.getImagem());
                return ps;
            }, keyHolder);

            Long id = keyHolder.getKey().longValue();
            return new Produto(id, produto.getNome(), produto.getDescricao(), produto.getPreco(), produto.getCategoria(), produto.isDisponibilidade(), produto.getImagem());
        } else {
            String sql = "UPDATE produto SET nome = ?, descricao = ?, preco = ?, categoria = ?, disponibilidade = ?, imagem = ? WHERE id = ?";
            jdbcTemplate.update(sql,
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
}
