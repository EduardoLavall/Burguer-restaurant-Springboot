package com.burguer.restaurant.repositorio;

import java.util.List;
import java.util.Optional;

import com.burguer.restaurant.dominio.produto.Produto;

public interface ProdutoRepositorio {

    List<Produto> listarTodos();

    Optional<Produto> buscarPorId(Long id);

    Produto salvar(Produto produto);
}
