package com.burguer.restaurant.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burguer.restaurant.dominio.produto.Produto;
import com.burguer.restaurant.dto.produto.ProdutoPrecoRequisicao;
import com.burguer.restaurant.dto.produto.ProdutoRequisicao;
import com.burguer.restaurant.dto.produto.ProdutoResposta;
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
    public ProdutoResposta criar(ProdutoRequisicao requisicao) {
        Produto produto = new Produto(
                null,
                requisicao.nome(),
                requisicao.descricao(),
                requisicao.preco(),
                requisicao.categoria(),
                requisicao.disponibilidade(),
                requisicao.imagem());

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
        // cria um novo objeto Produto com os valores atualizados e mesmo id
        Produto produto = new Produto(
                id,
                requisicao.nome(),
                requisicao.descricao(),
                requisicao.preco(),
                requisicao.categoria(),
                requisicao.disponibilidade(),
                requisicao.imagem());

        return paraResposta(produtoRepository.salvar(produto));
    }

    @Override
    public void remover(Long id) {
        Produto produto = buscarProdutoPorId(id);
        produto.remover();
        produtoRepository.salvar(produto);
    }

    private Produto buscarProdutoPorId(Long id) {
        return produtoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado para o id " + id));
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
