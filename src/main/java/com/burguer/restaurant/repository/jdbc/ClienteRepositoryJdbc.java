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

import com.burguer.restaurant.dominio.cliente.Cliente;
import com.burguer.restaurant.repository.ClienteRepository;

@Repository
public class ClienteRepositoryJdbc implements ClienteRepository {

    private static final String SQL_LISTAR_CLIENTES =
            "SELECT id, nome, email, endereco_entrega, preferencia_pagamento FROM cliente";
    private static final String SQL_BUSCAR_CLIENTE_POR_ID =
            SQL_LISTAR_CLIENTES + " WHERE id = ?";
    private static final String SQL_INSERIR_CLIENTE =
            "INSERT INTO cliente (nome, email, endereco_entrega, preferencia_pagamento) VALUES (?, ?, ?, ?)";
    private static final String SQL_ATUALIZAR_CLIENTE =
            "UPDATE cliente SET nome = ?, email = ?, endereco_entrega = ?, preferencia_pagamento = ? WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    public ClienteRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Cliente> listarTodos() {
        return jdbcTemplate.query(SQL_LISTAR_CLIENTES, (rs, rowNum) -> mapearCliente(rs));
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        var clientes = jdbcTemplate.query(SQL_BUSCAR_CLIENTE_POR_ID, (rs, rowNum) -> mapearCliente(rs), id);

        return clientes.stream().findFirst();
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        if (cliente.getId() == null) {
            return inserirCliente(cliente);
        } else {
            jdbcTemplate.update(SQL_ATUALIZAR_CLIENTE,
                    cliente.getNome(),
                    cliente.getEmail(),
                    cliente.getEnderecoEntrega(),
                    cliente.getPreferenciaPagamento(),
                    cliente.getId());
            return cliente;
        }
    }

    private Cliente inserirCliente(Cliente cliente) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERIR_CLIENTE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEmail());
            ps.setString(3, cliente.getEnderecoEntrega());
            ps.setString(4, cliente.getPreferenciaPagamento());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return new Cliente(id, cliente.getNome(), cliente.getEmail(), cliente.getEnderecoEntrega(),
                cliente.getPreferenciaPagamento());
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("endereco_entrega"),
                rs.getString("preferencia_pagamento"));
    }
}
