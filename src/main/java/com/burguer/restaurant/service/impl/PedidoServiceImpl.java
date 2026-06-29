package com.burguer.restaurant.service.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burguer.restaurant.dominio.pedido.ItemPedido;
import com.burguer.restaurant.dominio.pedido.Pedido;
import com.burguer.restaurant.dominio.pedido.StatusPedido;
import com.burguer.restaurant.dominio.produto.Produto;
import com.burguer.restaurant.dto.pedido.ItemPedidoRequisicao;
import com.burguer.restaurant.dto.pedido.ItemPedidoResposta;
import com.burguer.restaurant.dto.pedido.PedidoCheckoutRequisicao;
import com.burguer.restaurant.dto.pedido.PedidoResposta;
import com.burguer.restaurant.dto.pedido.PedidoStatusRequisicao;
import com.burguer.restaurant.exception.RegraNegocioException;
import com.burguer.restaurant.exception.RecursoNaoEncontradoException;
import com.burguer.restaurant.repository.PedidoRepository;
import com.burguer.restaurant.repository.ProdutoRepository;
import com.burguer.restaurant.service.PedidoService;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Override
    public List<PedidoResposta> listarTodos(StatusPedido status) {
        List<Pedido> pedidos = status == null
                ? pedidoRepository.listarTodos()
                : pedidoRepository.listarPorStatus(status);

        return pedidos.stream()
                .map(this::paraResposta)
                .toList();
    }

    @Override
    @Transactional
    public PedidoResposta criarCheckout(PedidoCheckoutRequisicao requisicao) {
        List<ItemPedido> itensPedido = montarItensPedido(requisicao);
        Pedido pedido = criarPedidoInicial(requisicao, itensPedido);
        return paraResposta(pedidoRepository.salvar(pedido));
    }

    @Override
    public PedidoResposta buscarPorId(Long id) {
        return paraResposta(buscarPedido(id));
    }

    @Override
    @Transactional
    public PedidoResposta atualizarStatus(Long id, PedidoStatusRequisicao requisicao) {
        Pedido pedido = buscarPedido(id).comStatus(requisicao.status(), OffsetDateTime.now());
        return paraResposta(pedidoRepository.salvar(pedido));
    }

    @Override
    @Transactional
    public void remover(Long id) {
        Pedido pedido = buscarPedido(id);

        // A exclusao fica restrita a pedidos encerrados para o painel nao apagar pedidos em andamento.
        if (pedido.getStatus() != StatusPedido.cancelado && pedido.getStatus() != StatusPedido.entregue) {
            throw new RegraNegocioException("So e permitido excluir pedidos cancelados ou entregues.");
        }

        pedidoRepository.removerPorId(id);
    }

    private Pedido buscarPedido(Long id) {
        return pedidoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido nao encontrado para o id " + id));
    }

    private List<ItemPedido> montarItensPedido(PedidoCheckoutRequisicao requisicao) {
        return requisicao.itens()
                .stream()
                .map(this::paraItemPedido)
                .toList();
    }

    private Pedido criarPedidoInicial(PedidoCheckoutRequisicao requisicao, List<ItemPedido> itensPedido) {
        OffsetDateTime agora = OffsetDateTime.now();

        return new Pedido(
                null,
                requisicao.nomeCliente().trim(),
                requisicao.numeroMesa(),
                itensPedido,
                StatusPedido.recebido,
                agora,
                agora);
    }

    private ItemPedido paraItemPedido(ItemPedidoRequisicao requisicao) {
        Produto produto = produtoRepository.buscarPorId(requisicao.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado para o id " + requisicao.produtoId()));

        // Produto inativo nao pode entrar no pedido mesmo que alguma tela antiga ainda esteja aberta.
        if (!produto.isDisponibilidade()) {
            throw new RegraNegocioException("Produto indisponivel para pedido: " + produto.getNome());
        }

        return new ItemPedido(produto, requisicao.quantidade());
    }

    private PedidoResposta paraResposta(Pedido pedido) {
        return new PedidoResposta(
                pedido.getId(),
                pedido.getNomeCliente(),
                pedido.getNumeroMesa(),
                montarItensResposta(pedido),
                pedido.getSubtotal(),
                pedido.getTaxaServico(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getDataCriacao(),
                pedido.getDataAtualizacao());
    }

    private List<ItemPedidoResposta> montarItensResposta(Pedido pedido) {
        return pedido.getItensPedido()
                .stream()
                .map(item -> new ItemPedidoResposta(
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getProduto().getPreco(),
                        item.getSubtotal()))
                .toList();
    }
}
