package com.burguer.restaurant.servico;

import java.util.List;

import com.burguer.restaurant.dto.cliente.ClienteRequisicao;
import com.burguer.restaurant.dto.cliente.ClienteResposta;

public interface ClienteServico {

    List<ClienteResposta> listarTodos();

    ClienteResposta criar(ClienteRequisicao requisicao);
}
