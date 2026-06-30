package com.burguer.restaurant.repository;

import java.math.BigDecimal;

public class Produto {

    public enum Categoria {
        comida,
        bebida,
        acompanhamento,
        doce
    }

    private final Long id;
    private final String nome;
    private final String descricao;
    private BigDecimal preco;
    private final Categoria categoria;
    private final boolean disponibilidade;
    private final String imagem;

    public Produto(Long id, String nome, String descricao, BigDecimal preco, Categoria categoria,
            boolean disponibilidade, String imagem) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.disponibilidade = disponibilidade;
        this.imagem = imagem;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public boolean isDisponibilidade() {
        return disponibilidade;
    }

    public String getImagem() {
        return imagem;
    }

    public void alterarPreco(BigDecimal novoPreco) {
        this.preco = novoPreco;
    }
}
