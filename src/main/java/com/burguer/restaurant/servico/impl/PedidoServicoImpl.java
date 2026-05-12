package com.burguer.restaurant.servico.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.burguer.restaurant.dominio.pedido.ItemPedido;
import com.burguer.restaurant.dominio.pedido.Pedido;
import com.burguer.restaurant.dominio.produto.Produto;
import com.burguer.restaurant.dto.pedido.ItemPedidoRequisicao;
import com.burguer.restaurant.dto.pedido.ItemPedidoResposta;
import com.burguer.restaurant.dto.pedido.PedidoRequisicao;
import com.burguer.restaurant.dto.pedido.PedidoResposta;
import com.burguer.restaurant.excecao.RecursoNaoEncontradoExcecao;
import com.burguer.restaurant.repositorio.ClienteRepositorio;
import com.burguer.restaurant.repositorio.PedidoRepositorio;
import com.burguer.restaurant.repositorio.ProdutoRepositorio;
import com.burguer.restaurant.servico.PedidoServico;

@Service
public class PedidoServicoImpl implements PedidoServico {

    private final PedidoRepositorio pedidoRepositorio;
    private final ProdutoRepositorio produtoRepositorio;
    private final ClienteRepositorio clienteRepositorio;

    public PedidoServicoImpl(
            PedidoRepositorio pedidoRepositorio,
            ProdutoRepositorio produtoRepositorio,
            ClienteRepositorio clienteRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.produtoRepositorio = produtoRepositorio;
        this.clienteRepositorio = clienteRepositorio;
    }

    @Override
    public List<PedidoResposta> listarTodos() {
        return pedidoRepositorio.listarTodos()
                .stream()
                .map(this::paraResposta)
                .toList();
    }

    @Override
    public PedidoResposta criar(PedidoRequisicao requisicao) {
        clienteRepositorio.buscarPorId(requisicao.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Cliente nao encontrado para o id " + requisicao.clienteId()));

        List<ItemPedido> itensPedido = requisicao.itensPedido()
                .stream()
                .map(this::paraItemPedido)
                .toList();

        // O pedido nasce com valor calculado pelo dominio para manter a regra em um ponto central.
        Pedido pedido = new Pedido(
                null,
                requisicao.clienteId(),
                itensPedido,
                requisicao.status(),
                OffsetDateTime.now(),
                null);

        return paraResposta(pedidoRepositorio.salvar(pedido));
    }

    private ItemPedido paraItemPedido(ItemPedidoRequisicao requisicao) {
        Produto produto = produtoRepositorio.buscarPorId(requisicao.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Produto nao encontrado para o id " + requisicao.produtoId()));

        return new ItemPedido(produto, requisicao.quantidade());
    }

    private PedidoResposta paraResposta(Pedido pedido) {
        List<ItemPedidoResposta> itensPedido = pedido.getItensPedido()
                .stream()
                .map(item -> new ItemPedidoResposta(
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getProduto().getPreco(),
                        item.getSubtotal()))
                .toList();

        return new PedidoResposta(
                pedido.getId(),
                pedido.getClienteId(),
                itensPedido,
                pedido.getSubtotal(),
                pedido.getTaxaServico(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getDataPedido(),
                pedido.getDataEntrega());
    }
}
