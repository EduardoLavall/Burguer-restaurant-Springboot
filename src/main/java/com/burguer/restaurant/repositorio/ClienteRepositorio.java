package com.burguer.restaurant.repositorio;

import java.util.List;
import java.util.Optional;

import com.burguer.restaurant.dominio.cliente.Cliente;

public interface ClienteRepositorio {

    List<Cliente> listarTodos();

    Optional<Cliente> buscarPorId(Long id);

    Cliente salvar(Cliente cliente);
}
