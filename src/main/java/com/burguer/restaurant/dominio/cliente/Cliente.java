package com.burguer.restaurant.dominio.cliente;

public class Cliente {

    private final Long id;
    private final String nome;
    private final String email;
    private final String enderecoEntrega;
    private final String preferenciaPagamento;

    public Cliente(Long id, String nome, String email, String enderecoEntrega, String preferenciaPagamento) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.enderecoEntrega = enderecoEntrega;
        this.preferenciaPagamento = preferenciaPagamento;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public String getPreferenciaPagamento() {
        return preferenciaPagamento;
    }
}
