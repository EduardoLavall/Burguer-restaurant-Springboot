package com.burguer.restaurant.repositorio.memoria;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.burguer.restaurant.dominio.pedido.Pedido;
import com.burguer.restaurant.repositorio.PedidoRepositorio;

@Repository
public class PedidoRepositorioMemoria implements PedidoRepositorio {

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
