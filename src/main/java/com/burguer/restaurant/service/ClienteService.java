package com.burguer.restaurant.service;

import java.util.List;

import com.burguer.restaurant.dto.cliente.ClienteRequisicao;
import com.burguer.restaurant.dto.cliente.ClienteResposta;

public interface ClienteService {

    List<ClienteResposta> listarTodos();

    ClienteResposta criar(ClienteRequisicao requisicao);
}
