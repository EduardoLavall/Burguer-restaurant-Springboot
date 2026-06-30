package com.burguer.restaurant.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burguer.restaurant.dto.PedidoDto;
import com.burguer.restaurant.repository.PedidoRepository;
import com.burguer.restaurant.repository.ProdutoRepository;
import com.burguer.restaurant.repository.PedidoRepository.ItemPedido;
import com.burguer.restaurant.repository.PedidoRepository.Pedido;
import com.burguer.restaurant.repository.ProdutoRepository.Produto;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<PedidoDto.Resposta> listarTodos(PedidoDto.Status status) {
        List<Pedido> pedidos = status == null
                ? pedidoRepository.listarTodos()
                : pedidoRepository.listarPorStatus(Pedido.Status.valueOf(status.name()));

        return pedidos.stream()
                .map(this::paraResposta)
                .toList();
    }

    @Transactional
    public PedidoDto.Resposta criarCheckout(PedidoDto.CheckoutRequisicao requisicao) {
        // O backend recalcula os itens e totais para nao confiar em valor vindo do tablet.
        List<ItemPedido> itensPedido = montarItensPedido(requisicao);
        Pedido pedido = criarPedidoInicial(requisicao, itensPedido);
        return paraResposta(pedidoRepository.salvar(pedido));
    }

    public PedidoDto.Resposta buscarPorId(Long id) {
        return paraResposta(buscarPedido(id));
    }

    @Transactional
    public PedidoDto.Resposta atualizarStatus(Long id, PedidoDto.AtualizacaoStatusRequisicao requisicao) {
        Pedido pedido = buscarPedido(id).comStatus(Pedido.Status.valueOf(requisicao.status().name()), OffsetDateTime.now());
        return paraResposta(pedidoRepository.salvar(pedido));
    }

    @Transactional
    public void remover(Long id) {
        Pedido pedido = buscarPedido(id);

        // A exclusao fica restrita a pedidos encerrados para o painel nao apagar pedidos em andamento.
        if (pedido.getStatus() != Pedido.Status.cancelado && pedido.getStatus() != Pedido.Status.entregue) {
            throw new IllegalArgumentException("So e permitido excluir pedidos cancelados ou entregues.");
        }

        pedidoRepository.removerPorId(id);
    }

    private Pedido buscarPedido(Long id) {
        return pedidoRepository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido nao encontrado para o id " + id));
    }

    private List<ItemPedido> montarItensPedido(PedidoDto.CheckoutRequisicao requisicao) {
        return requisicao.itens()
                .stream()
                .map(this::paraItemPedido)
                .toList();
    }

    private Pedido criarPedidoInicial(PedidoDto.CheckoutRequisicao requisicao, List<ItemPedido> itensPedido) {
        OffsetDateTime agora = OffsetDateTime.now();

        // Todo pedido novo comeca como recebido porque ainda vai entrar no fluxo da cozinha.
        return new Pedido(
                null,
                requisicao.nomeCliente().trim(),
                requisicao.numeroMesa(),
                itensPedido,
                Pedido.Status.recebido,
                agora,
                agora);
    }

    private ItemPedido paraItemPedido(PedidoDto.ItemRequisicao requisicao) {
        Produto produto = produtoRepository.buscarPorId(requisicao.produtoId())
                .orElseThrow(() -> new NoSuchElementException("Produto nao encontrado para o id " + requisicao.produtoId()));

        // Produto inativo nao pode entrar no pedido mesmo que alguma tela antiga ainda esteja aberta.
        if (!produto.isDisponibilidade()) {
            throw new IllegalArgumentException("Produto indisponivel para pedido: " + produto.getNome());
        }

        return new ItemPedido(produto, requisicao.quantidade());
    }

    private PedidoDto.Resposta paraResposta(Pedido pedido) {
        // A resposta ja sai pronta para a tela, sem expor o modelo interno direto para o controller.
        return new PedidoDto.Resposta(
                pedido.getId(),
                pedido.getNomeCliente(),
                pedido.getNumeroMesa(),
                montarItensResposta(pedido),
                pedido.getSubtotal(),
                pedido.getTaxaServico(),
                pedido.getValorTotal(),
                PedidoDto.Status.valueOf(pedido.getStatus().name()),
                pedido.getDataCriacao(),
                pedido.getDataAtualizacao());
    }

    private List<PedidoDto.ItemResposta> montarItensResposta(Pedido pedido) {
        return pedido.getItensPedido()
                .stream()
                .map(item -> new PedidoDto.ItemResposta(
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getProduto().getPreco(),
                        item.getSubtotal()))
                .toList();
    }
}
