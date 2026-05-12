package com.burguer.restaurant.repositorio.memoria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.burguer.restaurant.dominio.produto.Produto;
import com.burguer.restaurant.repositorio.ProdutoRepositorio;

@Repository
public class ProdutoRepositorioMemoria implements ProdutoRepositorio {

    private final ConcurrentHashMap<Long, Produto> armazenamento = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    @Override
    public List<Produto> listarTodos() {
        return new ArrayList<>(armazenamento.values());
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public Produto salvar(Produto produto) {
        Long id = produto.getId() == null ? sequencia.incrementAndGet() : produto.getId();
        Produto produtoArmazenado = new Produto(
                id,
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getCategoria(),
                produto.isDisponibilidade(),
                produto.getImagem());
        armazenamento.put(id, produtoArmazenado);
        return produtoArmazenado;
    }
}
