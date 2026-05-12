package com.burguer.restaurant.dominio.produto;

import java.math.BigDecimal;

public class Produto {

    private final Long id;
    private final String nome;
    private final String descricao;
    private BigDecimal preco;
    private final CategoriaProduto categoria;
    private boolean disponibilidade;
    private String imagem;

    public Produto(Long id, String nome, String descricao, BigDecimal preco, CategoriaProduto categoria,
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

    public CategoriaProduto getCategoria() {
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

    public void remover() {
        this.disponibilidade = false;
    }

    public void atualizarImagem(String novaImagem) {
        this.imagem = novaImagem;
    }
}
