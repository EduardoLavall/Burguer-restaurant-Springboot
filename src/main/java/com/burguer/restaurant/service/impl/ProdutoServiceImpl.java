package com.burguer.restaurant.service.impl;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.burguer.restaurant.dominio.produto.Produto;
import com.burguer.restaurant.dto.cardapio.CardapioProdutoResposta;
import com.burguer.restaurant.dto.produto.ProdutoPrecoRequisicao;
import com.burguer.restaurant.dto.produto.ProdutoRequisicao;
import com.burguer.restaurant.dto.produto.ProdutoResposta;
import com.burguer.restaurant.dto.produto.ProdutoStatusRequisicao;
import com.burguer.restaurant.exception.RegraNegocioException;
import com.burguer.restaurant.exception.RecursoNaoEncontradoException;
import com.burguer.restaurant.repository.ProdutoRepository;
import com.burguer.restaurant.service.ProdutoService;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoServiceImpl(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    public List<ProdutoResposta> listarTodos() {
        return produtoRepository.listarTodos()
                .stream()
                .map(this::paraResposta)
                .toList();
    }

    @Override
    public List<CardapioProdutoResposta> listarCardapio() {
        return produtoRepository.listarTodos()
                .stream()
                .filter(Produto::isDisponibilidade)
                .map(this::paraCardapioResposta)
                .toList();
    }

    @Override
    public ProdutoResposta criar(ProdutoRequisicao requisicao) {
        Produto produto = criarProduto(null, requisicao);
        return paraResposta(produtoRepository.salvar(produto));
    }

    @Override
    public ProdutoResposta alterarPreco(Long id, ProdutoPrecoRequisicao requisicao) {
        Produto produto = buscarProdutoPorId(id);
        produto.alterarPreco(requisicao.preco());
        return paraResposta(produtoRepository.salvar(produto));
    }

    @Override
    public ProdutoResposta atualizar(Long id, ProdutoRequisicao requisicao) {
        buscarProdutoPorId(id);
        Produto produto = criarProduto(id, requisicao);
        return paraResposta(produtoRepository.salvar(produto));
    }

    @Override
    public ProdutoResposta atualizarStatus(Long id, ProdutoStatusRequisicao requisicao) {
        Produto produto = buscarProdutoPorId(id);
        Produto produtoAtualizado = new Produto(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getCategoria(),
                requisicao.disponibilidade(),
                produto.getImagem());
        return paraResposta(produtoRepository.salvar(produtoAtualizado));
    }

    @Override
    public void remover(Long id) {
        buscarProdutoPorId(id);

        try {
            produtoRepository.removerPorId(id);
        } catch (DataIntegrityViolationException excecao) {
            throw new RegraNegocioException("Nao e permitido excluir produto que ja faz parte de pedidos.");
        }
    }

    private Produto buscarProdutoPorId(Long id) {
        return produtoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado para o id " + id));
    }

    private Produto criarProduto(Long id, ProdutoRequisicao requisicao) {
        return new Produto(
                id,
                requisicao.nome(),
                requisicao.descricao(),
                requisicao.preco(),
                requisicao.categoria(),
                requisicao.disponibilidade(),
                requisicao.imagem());
    }

    private CardapioProdutoResposta paraCardapioResposta(Produto produto) {
        return new CardapioProdutoResposta(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getCategoria(),
                produto.getImagem());
    }

    private ProdutoResposta paraResposta(Produto produto) {
        return new ProdutoResposta(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getCategoria(),
                produto.isDisponibilidade(),
                produto.getImagem());
    }
}
