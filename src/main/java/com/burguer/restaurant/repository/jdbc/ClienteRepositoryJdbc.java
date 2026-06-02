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

import com.burguer.restaurant.dominio.cliente.Cliente;
import com.burguer.restaurant.repository.ClienteRepository;

@Repository
@Profile("sqlite")
public class ClienteRepositoryJdbc implements ClienteRepository {

    private final JdbcTemplate jdbcTemplate;

    public ClienteRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Cliente> listarTodos() {
        return jdbcTemplate.query("SELECT id, nome, email, endereco_entrega, preferencia_pagamento FROM cliente",
                (rs, rowNum) -> new Cliente(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("endereco_entrega"),
                        rs.getString("preferencia_pagamento")
                ));
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        var list = jdbcTemplate.query("SELECT id, nome, email, endereco_entrega, preferencia_pagamento FROM cliente WHERE id = ?",
                (rs, rowNum) -> new Cliente(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("endereco_entrega"),
                        rs.getString("preferencia_pagamento")
                ), id);

        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        if (cliente.getId() == null) {
            String sql = "INSERT INTO cliente (nome, email, endereco_entrega, preferencia_pagamento) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, cliente.getNome());
                ps.setString(2, cliente.getEmail());
                ps.setString(3, cliente.getEnderecoEntrega());
                ps.setString(4, cliente.getPreferenciaPagamento());
                return ps;
            }, keyHolder);

            Long id = keyHolder.getKey().longValue();
            return new Cliente(id, cliente.getNome(), cliente.getEmail(), cliente.getEnderecoEntrega(), cliente.getPreferenciaPagamento());
        } else {
            String sql = "UPDATE cliente SET nome = ?, email = ?, endereco_entrega = ?, preferencia_pagamento = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    cliente.getNome(),
                    cliente.getEmail(),
                    cliente.getEnderecoEntrega(),
                    cliente.getPreferenciaPagamento(),
                    cliente.getId());
            return cliente;
        }
    }
}
