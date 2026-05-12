package com.burguer.restaurant.servico.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burguer.restaurant.dominio.cliente.Cliente;
import com.burguer.restaurant.dto.cliente.ClienteRequisicao;
import com.burguer.restaurant.dto.cliente.ClienteResposta;
import com.burguer.restaurant.repositorio.ClienteRepositorio;
import com.burguer.restaurant.servico.ClienteServico;

@Service
public class ClienteServicoImpl implements ClienteServico {

    private final ClienteRepositorio clienteRepositorio;

    public ClienteServicoImpl(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    @Override
    public List<ClienteResposta> listarTodos() {
        return clienteRepositorio.listarTodos()
                .stream()
                .map(this::paraResposta)
                .toList();
    }

    @Override
    public ClienteResposta criar(ClienteRequisicao requisicao) {
        Cliente cliente = new Cliente(
                null,
                requisicao.nome(),
                requisicao.email(),
                requisicao.enderecoEntrega(),
                requisicao.preferenciaPagamento());

        return paraResposta(clienteRepositorio.salvar(cliente));
    }

    private ClienteResposta paraResposta(Cliente cliente) {
        return new ClienteResposta(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getEnderecoEntrega(),
                cliente.getPreferenciaPagamento());
    }
}
