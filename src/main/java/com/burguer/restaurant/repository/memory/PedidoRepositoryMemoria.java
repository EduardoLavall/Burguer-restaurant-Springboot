package com.burguer.restaurant.repository.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import com.burguer.restaurant.dominio.pedido.Pedido;
import com.burguer.restaurant.repository.PedidoRepository;

@Repository
@Profile("!sqlite")
public class PedidoRepositoryMemoria implements PedidoRepository {

    private final List<Pedido> armazenamento = new CopyOnWriteArrayList<>();
    private final AtomicLong sequencia = new AtomicLong();

    @Override
    public List<Pedido> listarTodos() {
        return new ArrayList<>(armazenamento);
    }

    @Override
    public Pedido salvar(Pedido pedido) {
        Pedido pedidoArmazenado = new Pedido(
                sequencia.incrementAndGet(),
                pedido.getClienteId(),
                pedido.getItensPedido(),
                pedido.getStatus(),
                pedido.getDataPedido(),
                pedido.getDataEntrega());
        armazenamento.add(pedidoArmazenado);
        return pedidoArmazenado;
    }
}
