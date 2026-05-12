package com.burguer.restaurant.service.impl;

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
import com.burguer.restaurant.exception.RecursoNaoEncontradoException;
import com.burguer.restaurant.repository.ClienteRepository;
import com.burguer.restaurant.repository.PedidoRepository;
import com.burguer.restaurant.repository.ProdutoRepository;
import com.burguer.restaurant.service.PedidoService;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoServiceImpl(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<PedidoResposta> listarTodos() {
        return pedidoRepository.listarTodos()
                .stream()
                .map(this::paraResposta)
                .toList();
    }

    @Override
    public PedidoResposta criar(PedidoRequisicao requisicao) {
        clienteRepository.buscarPorId(requisicao.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado para o id " + requisicao.clienteId()));

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

        return paraResposta(pedidoRepository.salvar(pedido));
    }

    private ItemPedido paraItemPedido(ItemPedidoRequisicao requisicao) {
        Produto produto = produtoRepository.buscarPorId(requisicao.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado para o id " + requisicao.produtoId()));

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
