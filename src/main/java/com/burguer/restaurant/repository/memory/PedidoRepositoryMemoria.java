package com.burguer.restaurant.repository.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.burguer.restaurant.dominio.pedido.Pedido;
import com.burguer.restaurant.dominio.pedido.StatusPedido;
import com.burguer.restaurant.repository.PedidoRepository;

@Repository
@Profile("memory")
public class PedidoRepositoryMemoria implements PedidoRepository {

    private final List<Pedido> armazenamento = new CopyOnWriteArrayList<>();
    private final AtomicLong sequencia = new AtomicLong();

    @Override
    public List<Pedido> listarTodos() {
        return new ArrayList<>(armazenamento);
    }

    @Override
    public List<Pedido> listarPorStatus(StatusPedido status) {
        return armazenamento.stream()
                .filter(pedido -> pedido.getStatus() == status)
                .toList();
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return armazenamento.stream()
                .filter(pedido -> pedido.getId().equals(id))
                .findFirst();
    }

    @Override
    public Pedido salvar(Pedido pedido) {
        Pedido pedidoArmazenado = new Pedido(
                pedido.getId() == null ? sequencia.incrementAndGet() : pedido.getId(),
                pedido.getNomeCliente(),
                pedido.getNumeroMesa(),
                pedido.getItensPedido(),
                pedido.getStatus(),
                pedido.getDataCriacao(),
                pedido.getDataAtualizacao());

        armazenamento.removeIf(item -> item.getId().equals(pedidoArmazenado.getId()));
        armazenamento.add(pedidoArmazenado);
        return pedidoArmazenado;
    }

    @Override
    public void removerPorId(Long id) {
        armazenamento.removeIf(pedido -> pedido.getId().equals(id));
    }
}
