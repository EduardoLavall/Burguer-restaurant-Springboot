package com.burguer.restaurant.repository.memory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import com.burguer.restaurant.dominio.produto.CategoriaProduto;
import com.burguer.restaurant.dominio.produto.Produto;
import com.burguer.restaurant.repository.ProdutoRepository;

@Repository
@Profile("memory")
public class ProdutoRepositoryMemoria implements ProdutoRepository {

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

    @Override
    public void removerPorId(Long id) {
        armazenamento.remove(id);
    }

    @PostConstruct
    public void carregarProdutosIniciais() {
        if (!armazenamento.isEmpty()) {
            return;
        }

        salvar(new Produto(
                null,
                "Classico Bacon",
                "Pao brioche, hamburguer 180g, queijo cheddar, bacon crocante e maionese especial.",
                new BigDecimal("29.90"),
                CategoriaProduto.comida,
                true,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQNMEyVsK2Sb0hq8ZJDPBRQkKzwZ2lICuPAqw&s"));

        salvar(new Produto(
                null,
                "Duplo Cheddar",
                "Hamburguer com queijo cheddar duplo, picles e molho especial.",
                new BigDecimal("32.90"),
                CategoriaProduto.comida,
                true,
                null));

        salvar(new Produto(
                null,
                "Refrigerante Cola",
                "Refrigerante sabor cola gelado.",
                new BigDecimal("7.00"),
                CategoriaProduto.bebida,
                true,
                null));

        salvar(new Produto(
                null,
                "Batata Frita",
                "Porcao de batata frita crocante com sal e molho da casa.",
                new BigDecimal("14.90"),
                CategoriaProduto.acompanhamento,
                true,
                null));
    }
}
