package com.burguer.restaurant.repository;

import java.util.List;
import java.util.Optional;

import com.burguer.restaurant.dominio.produto.Produto;

public interface ProdutoRepository {

    List<Produto> listarTodos();

    Optional<Produto> buscarPorId(Long id);

    Produto salvar(Produto produto);

    void removerPorId(Long id);
}
