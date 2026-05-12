package com.burguer.restaurant.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burguer.restaurant.dominio.cliente.Cliente;
import com.burguer.restaurant.dto.cliente.ClienteRequisicao;
import com.burguer.restaurant.dto.cliente.ClienteResposta;
import com.burguer.restaurant.repository.ClienteRepository;
import com.burguer.restaurant.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<ClienteResposta> listarTodos() {
        return clienteRepository.listarTodos()
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

        return paraResposta(clienteRepository.salvar(cliente));
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
