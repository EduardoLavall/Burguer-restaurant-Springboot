package com.burguer.restaurant.repository.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.burguer.restaurant.dominio.cliente.Cliente;
import com.burguer.restaurant.repository.ClienteRepository;

@Repository
public class ClienteRepositoryMemoria implements ClienteRepository {

    private final ConcurrentHashMap<Long, Cliente> armazenamento = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(armazenamento.values());
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        Long id = cliente.getId() == null ? sequencia.incrementAndGet() : cliente.getId();
        Cliente clienteArmazenado = new Cliente(
                id,
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getEnderecoEntrega(),
                cliente.getPreferenciaPagamento());
        armazenamento.put(id, clienteArmazenado);
        return clienteArmazenado;
    }
}
