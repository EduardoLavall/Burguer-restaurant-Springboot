package com.burguer.restaurant.servico.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burguer.restaurant.dominio.produto.Produto;
import com.burguer.restaurant.dto.produto.ProdutoRequisicao;
import com.burguer.restaurant.dto.produto.ProdutoResposta;
import com.burguer.restaurant.repositorio.ProdutoRepositorio;
import com.burguer.restaurant.servico.ProdutoServico;

@Service
public class ProdutoServicoImpl implements ProdutoServico {

    private final ProdutoRepositorio produtoRepositorio;

    public ProdutoServicoImpl(ProdutoRepositorio produtoRepositorio) {
        this.produtoRepositorio = produtoRepositorio;
    }

    @Override
    public List<ProdutoResposta> listarTodos() {
        return produtoRepositorio.listarTodos()
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

        return paraResposta(produtoRepositorio.salvar(produto));
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
