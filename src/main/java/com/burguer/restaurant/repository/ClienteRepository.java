package com.burguer.restaurant.repository;

import java.util.List;
import java.util.Optional;

import com.burguer.restaurant.dominio.cliente.Cliente;

public interface ClienteRepository {

    List<Cliente> listarTodos();

    Optional<Cliente> buscarPorId(Long id);

    Cliente salvar(Cliente cliente);
}
