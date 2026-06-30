package com.burguer.restaurant.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.burguer.restaurant.dto.ProdutoDto;
import com.burguer.restaurant.repository.Produto;
import com.burguer.restaurant.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<ProdutoDto.Resposta> listarTodos() {
        return produtoRepository.listarTodos()
                .stream()
                .map(this::paraResposta)
                .toList();
    }

    public List<ProdutoDto.CardapioResposta> listarCardapio() {
        return produtoRepository.listarTodos()
                // O cardapio do cliente mostra so o que esta ativo no momento.
                .stream()
                .filter(Produto::isDisponibilidade)
                .map(this::paraCardapioResposta)
                .toList();
    }

    public ProdutoDto.Resposta criar(ProdutoDto.Requisicao requisicao) {
        Produto produto = criarProduto(null, requisicao);
        return paraResposta(produtoRepository.salvar(produto));
    }

    public ProdutoDto.Resposta alterarPreco(Long id, ProdutoDto.AtualizacaoPreco requisicao) {
        Produto produto = buscarProdutoPorId(id);
        produto.alterarPreco(requisicao.preco());
        return paraResposta(produtoRepository.salvar(produto));
    }

    public ProdutoDto.Resposta atualizar(Long id, ProdutoDto.Requisicao requisicao) {
        buscarProdutoPorId(id);
        Produto produto = criarProduto(id, requisicao);
        return paraResposta(produtoRepository.salvar(produto));
    }

    public ProdutoDto.Resposta atualizarStatus(Long id, ProdutoDto.AtualizacaoStatus requisicao) {
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

    public void remover(Long id) {
        buscarProdutoPorId(id);

        try {
            produtoRepository.removerPorId(id);
        } catch (DataIntegrityViolationException excecao) {
            // Se o produto ja apareceu em pedido, eu mantenho o historico e bloqueio a exclusao fisica.
            throw new IllegalArgumentException("Nao e permitido excluir produto que ja faz parte de pedidos.");
        }
    }

    private Produto buscarProdutoPorId(Long id) {
        return produtoRepository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Produto nao encontrado para o id " + id));
    }

    private Produto criarProduto(Long id, ProdutoDto.Requisicao requisicao) {
        return new Produto(
                id,
                requisicao.nome(),
                requisicao.descricao(),
                requisicao.preco(),
                Produto.Categoria.valueOf(requisicao.categoria().name()),
                requisicao.disponibilidade(),
                requisicao.imagem());
    }

    private ProdutoDto.CardapioResposta paraCardapioResposta(Produto produto) {
        return new ProdutoDto.CardapioResposta(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                ProdutoDto.Categoria.valueOf(produto.getCategoria().name()),
                produto.getImagem());
    }

    private ProdutoDto.Resposta paraResposta(Produto produto) {
        return new ProdutoDto.Resposta(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                ProdutoDto.Categoria.valueOf(produto.getCategoria().name()),
                produto.isDisponibilidade(),
                produto.getImagem());
    }
}
